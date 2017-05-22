package de.viadee.bpm.vPAV;

import java.net.URL;

public class PathFinder {

    private ClassLoader classLoader;

    public PathFinder(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * read resource file
     * 
     * @param fileName
     * @return path of file
     */
    public String readResourceFile(final String fileName) {
        String pathResourceFile = "";
        if (fileName != null && fileName.trim().length() > 0) {
            final URL resource = classLoader.getResource(fileName);
            if (resource != null) {
                pathResourceFile = resource.toString();
                pathResourceFile = pathResourceFile.substring(pathResourceFile.indexOf("/") + 1); // remove file typ
            }
        }
        return pathResourceFile;
    }
}
