package com.example.training;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

//@formatter:off
/**
 * Demonstrates the Java 20.0.1 change to File.listRoots() on Windows.
 *
 * Before 20.0.1:
 *   File.listRoots() only returned drives that were currently accessible.
 *
 * Since 20.0.1 (Windows only):
 *   File.listRoots() returns ALL available drive roots, including:
 *   - empty optical drives
 *   - card readers with no card inserted
 *   - unmounted but visible volumes
 *
 * This now matches:
 *   FileSystems.getDefault().getRootDirectories()
 */
//@formatter:on
public class FileRootsBehaviorDemo {

    public static void main(String[] args) {
        System.out.println("File.listRoots():");
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                System.out.println("  " + root);
            }
        }

        System.out.println();
        System.out.println("FileSystem.getDefault().getRootDirectories():");
        FileSystem fs = FileSystems.getDefault();
        for (var root : fs.getRootDirectories()) {
            System.out.println("  " + root);
        }
    }
}
