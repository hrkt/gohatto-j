package io.hrkt.gohatto.rule;

import io.hrkt.gohatto.exception.GohattoApplicationException;
import lombok.val;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DoNotUseForbiddenPackage implements Rule {
    static final Logger LOG = LoggerFactory.getLogger(DoNotUseForbiddenPackage.class);
    static final private String ruleName = "DoNotUseForbiddenPackage";
    private String jarFilePath;
    private List<Pattern> patterns;

    public DoNotUseForbiddenPackage(String jarFilePath, String[] forbiddenPackages) {
        this.jarFilePath = jarFilePath;

        patterns = Collections.unmodifiableList(
                Arrays.stream(forbiddenPackages)
                        .map(s -> Pattern.compile(s))
                        .collect(Collectors.toList()));
        LOG.debug(patterns.size() + " patterns.");
        patterns.stream().forEach(p -> {
            LOG.debug("PATTERN:" + p.pattern());
        });
    }

    @Override
    public List<Result> apply() {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            val e = jarFile.entries();

            val urls = new URL[]{new URL("jar:file:" + jarFilePath + "!/")};
            val ucl = URLClassLoader.newInstance(urls);
            val repository = new ClassLoaderRepository(ucl);

            return Collections.list(e).stream()
                    .filter(je -> !je.isDirectory())
                    .filter(je -> je.getName().endsWith(".class"))
                    .flatMap(je -> check(repository, je).stream())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new GohattoApplicationException(e);
        }
    }

    private List<String> extractReferencedClassNames(ClassLoaderRepository repository, String fqcn) {
        JavaClass clazz = null;
        try {
            clazz = repository.loadClass(fqcn);
            val cp = clazz.getConstantPool();
            int cpLength = cp.getLength();

            val referencedClasses = new ArrayList<ConstantUtf8>();
            IntStream.range(0, cpLength).forEach(i -> {
                val constant = cp.getConstant(i);
                if (null == constant || !(constant instanceof ConstantClass)) {
                    // do nothing
                } else {
                    val constantClass = (ConstantClass) constant;
                    val referencedConstant = cp.getConstant(constantClass.getNameIndex());
                    referencedClasses.add((ConstantUtf8) referencedConstant);
                }
            });
            val tc = referencedClasses.stream()
                    .map(c -> c.getBytes())
                    .map(s -> s.replace("/", "."))
                    .collect(Collectors.toList());
            return tc;
        } catch (ClassNotFoundException e) {
            throw new GohattoApplicationException(e);
        }
    }

    private Stream<Result> applyRegex(String fqcn) {
        return patterns.stream()
                .map(p -> p.matcher(fqcn))
                .filter(m -> m.find())
                .map(m -> new SimpleResult(Result.ResultType.ERROR, fqcn + " violates rule", ruleName));
    }

    private List<Result> check(ClassLoaderRepository repository, JarEntry je) {
        val className = je.getName().substring(0, je.getName().length() - ".class".length());
        val replacedFqcn = className.replace('/', '.');

        val classNames = extractReferencedClassNames(repository, replacedFqcn);
        classNames.stream().forEach(LOG::debug);
        return classNames.stream().flatMap(fqcn -> applyRegex(fqcn)).collect(Collectors.toList());
    }
}
