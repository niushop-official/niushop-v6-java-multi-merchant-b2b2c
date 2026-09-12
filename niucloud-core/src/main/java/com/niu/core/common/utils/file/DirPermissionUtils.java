package com.niu.core.common.utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 目录及子目录读写权限检测（对应 PHP checkDirPermissions）
 */
public final class DirPermissionUtils {

    private DirPermissionUtils() {
    }

    public static class DirPermissionResult {
        private final List<String> unreadable = new ArrayList<>();
        private final List<String> notWritable = new ArrayList<>();

        public List<String> getUnreadable() {
            return unreadable;
        }

        public List<String> getNotWritable() {
            return notWritable;
        }

        public void merge(DirPermissionResult other) {
            if (other == null) {
                return;
            }
            unreadable.addAll(other.unreadable);
            notWritable.addAll(other.notWritable);
        }
    }

    public static DirPermissionResult checkDirPermissions(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            throw new RuntimeException(String.format("指定的路径 \"%s\" 不是一个有效的目录", dirPath));
        }
        return checkDirPermissions(dir, new DirPermissionResult());
    }

    public static DirPermissionResult checkDirPermissionsIfExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            return new DirPermissionResult();
        }
        return checkDirPermissions(dir, new DirPermissionResult());
    }

    private static DirPermissionResult checkDirPermissions(File dir, DirPermissionResult data) {
        try {
            if (!dir.canRead()) {
                data.unreadable.add(dir.getPath());
            }
            if (!dir.canWrite()) {
                data.notWritable.add(dir.getPath());
            }
            if (!dir.canRead()) {
                return data;
            }
            File[] children = dir.listFiles();
            if (children == null) {
                return data;
            }
            for (File child : children) {
                if (child.isDirectory()) {
                    checkDirPermissions(child, data);
                } else {
                    if (!child.canRead()) {
                        data.unreadable.add(child.getPath());
                    }
                    if (!child.canWrite()) {
                        data.notWritable.add(child.getPath());
                    }
                }
            }
        } catch (Exception e) {
            data.unreadable.add(dir.getPath());
            data.notWritable.add(dir.getPath());
        }
        return data;
    }
}
