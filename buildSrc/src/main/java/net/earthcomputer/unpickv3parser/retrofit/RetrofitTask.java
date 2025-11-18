package net.earthcomputer.unpickv3parser.retrofit;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public abstract class RetrofitTask extends DefaultTask {
    @InputFile
    public abstract RegularFileProperty getInputFile();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void run() throws IOException {
        String java17PathStr = System.getProperty("java.17.home");
        if (java17PathStr == null) {
            throw new IllegalStateException("java.17.home property not set");
        }

        try (Retrofitter retrofitter = new Retrofitter(Path.of(java17PathStr));
             ZipFile zin = new ZipFile(getInputFile().get().getAsFile());
             ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(getOutputFile().get().getAsFile()))
        ) {
            Enumeration<? extends ZipEntry> entries = zin.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    zout.putNextEntry(entry);
                    zout.closeEntry();
                } else {
                    zout.putNextEntry(new ZipEntry(entry.getName()));
                    if (entry.getName().endsWith(".class")) {
                        ClassReader reader = new ClassReader(zin.getInputStream(entry));
                        ClassWriter writer = new ClassWriter(0);
                        reader.accept(retrofitter.createClassVisitor(writer), 0);
                        zout.write(writer.toByteArray());
                    } else {
                        zin.getInputStream(entry).transferTo(zout);
                    }
                    zout.closeEntry();
                }
            }
        }
    }
}
