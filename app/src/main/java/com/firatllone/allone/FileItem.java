package com.firatllone.allone;

public class FileItem {
    private String name;
    private boolean isDirectory;
    private int iconResId; // İkon kaynak ID'si ekleyin

    // Yeni yapıcı
    public FileItem(String name, boolean isDirectory, int iconResId) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.iconResId = iconResId; // Yeni parametreyi ayarlayın
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public int getIconResId() {
        return iconResId; // İkon kaynak ID'sini döndürmek için bir yöntem ekleyin
    }
}
