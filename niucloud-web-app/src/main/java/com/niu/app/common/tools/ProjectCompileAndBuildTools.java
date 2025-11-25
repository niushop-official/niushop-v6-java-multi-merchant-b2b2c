package com.niu.app.common.tools;

import com.niu.core.service.core.addon.AddonInstallTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProjectCompileAndBuildTools {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String projectHome = ProjectCompileAndBuildTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (projectHome.contains("niucloud-web-app")) {
            projectHome = projectHome.substring(0, projectHome.indexOf("niucloud-web-app"));
        } else {
            System.err.println("工程[niucloud-web-app]不存在.");
            return;
        }
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        boolean installAndBuild = false;
        boolean srcBuild = false;
        for (String arg : args) {
            if ("all".equals(arg)) {
                installAndBuild = true;
                srcBuild = true;
            }
            if ("compile".equals(arg)) {
                installAndBuild = true;
            }
            if ("build".equals(arg)) {
                srcBuild = true;
            }
        }
        // 打包编译
        if (installAndBuild) {
//            installAndBuildAdmin(projectHome);
//            installAndBuildH5(projectHome);
//            installAndBuildWeb(projectHome);
        }
        // 工程源码复制
        if (srcBuild) {
            buildAdminToRuntime(projectHome);
            buildH5ToRuntime(projectHome);
            buildWebToRuntime(projectHome);
        }
    }


    /**
     * @param home
     */
    public static void installAndBuildAdmin(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Installing admin");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int installResult = execute(home + "/admin", "npm install");
        System.out.println();
        if (installResult == 0) {
            System.out.println("[INFO] Installing SUCCESS");
        } else {
            System.out.println("[INFO] Installing ERROR");
        }
        // 检查目标目录是否存在
        File distFile = new File(home + "/webroot/public/admin/");
        if (!distFile.exists()) {
            System.out.println("[INFO] create wap directory");
            distFile.mkdirs();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] BUILD admin");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int buildResult = execute(home + "/admin", "npm run build");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        if (buildResult == 0) {
            System.out.println("[INFO] BUILD SUCCESS");
        } else {
            System.out.println("[INFO] BUILD ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    public static void installAndBuildH5(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Installing uni-app");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int installResult = execute(home + "/uni-app", "npm install");
        System.out.println();
        if (installResult == 0) {
            System.out.println("[INFO] Installing SUCCESS");
        } else {
            System.out.println("[INFO] Installing ERROR");
        }
        // 检查目标目录是否存在
        File distFile = new File(home + "/webroot/public/wap/");
        if (!distFile.exists()) {
            System.out.println("[INFO] create wap directory");
            distFile.mkdirs();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] BUILD uni-app");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int buildResult = execute(home + "/uni-app", "npm run build:h5");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        if (buildResult == 0) {
            System.out.println("[INFO] BUILD SUCCESS");
        } else {
            System.out.println("[INFO] BUILD ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    public static void installAndBuildWeb(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Installing web");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int installResult = execute(home + "/web", "npm install");
        System.out.println();
        if (installResult == 0) {
            System.out.println("[INFO] Installing SUCCESS");
        } else {
            System.out.println("[INFO] Installing ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] BUILD web");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        // 检查目标目录是否存在
        File distFile = new File(home + "/webroot/public/web/");
        if (!distFile.exists()) {
            System.out.println("[INFO] create wap directory");
            distFile.mkdirs();
        }
//        try {
//            System.setOut(new PrintStream(System.out, true, "GBK"));
//        } catch (UnsupportedEncodingException e) {
//            // ignore
//        }
        int buildResult = execute(home + "/web", "npm run generate");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        if (buildResult == 0) {
            System.out.println("[INFO] BUILD SUCCESS");
        } else {
            System.out.println("[INFO] BUILD ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     * @param command
     */
    public static int execute(String home, String command) {
        if (System.getProperty("os.name").contains("Windows")) {
            return executeWithWin(home, command);
        } else {
            return executeWithLinux(home, command);
        }
    }

    /**
     * @param home
     * @param command
     * @return
     */
    public static int executeWithWin(String home, String command) {
        Process operateProcess = null;
        try {
            Process process = Runtime.getRuntime().exec("cmd /c " + command, null, new File(home));
            operateProcess = process;
            new Thread(() -> {
                BufferedReader standardReader = null;
                try {
                    standardReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = standardReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        standardReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            new Thread(() -> {
                BufferedReader errorReader = null;
                try {
                    errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        errorReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                operateProcess.destroyForcibly();
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    /**
     * @param home
     * @param command
     * @return
     */
    public static int executeWithLinux(String home, String command) {
        Process operateProcess = null;
        try {
            Process process = Runtime.getRuntime().exec(command, null, new File(home));
            operateProcess = process;
            new Thread(() -> {
                BufferedReader standardReader = null;
                try {
                    standardReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = standardReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        standardReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            new Thread(() -> {
                BufferedReader errorReader = null;
                try {
                    errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        errorReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                operateProcess.destroyForcibly();
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    /**
     * @param home
     */
    private static void buildAdminToRuntime(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying admin start ->");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        try {
            File srcDir = new File(home + "/admin");
            File destDir = new File(home + "/webroot/runtime/admin");
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }

            String[] exclusionDir = {"node_modules", ".vscode", "dist", ".idea"};
            FileUtils.copyDirectory(srcDir, destDir, createExclusionFilter(exclusionDir));

            // 移除插件
            File addonDir = new File(home + "/webroot/runtime/admin/src/addon");
            Collection<File> addonDirFile = FileUtils.listFilesAndDirs(
                    addonDir,
                    TrueFileFilter.INSTANCE,   // 匹配所有文件
                    TrueFileFilter.INSTANCE    // 匹配所有目录
            );
            for (File file : addonDirFile) {
                // 跳过根目录和商城
                if (file.equals(addonDir) || file.getName().equals("mall")) {
                    continue;
                }
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying admin ended <-");
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    private static void buildH5ToRuntime(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying uni-app start ->");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        try {
            File srcDir = new File(home + "/uni-app");
            File destDir = new File(home + "/webroot/runtime/uni-app");
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }

            String[] exclusionDir = {"node_modules", "unpackage", "dist", ".hbuilderx"};
            FileUtils.copyDirectory(srcDir, destDir, createExclusionFilter(exclusionDir));

            // 移除插件
            File addonDir = new File(home + "/webroot/runtime/uni-app/src/addon");
            Collection<File> addonDirFile = FileUtils.listFilesAndDirs(
                    addonDir,
                    TrueFileFilter.INSTANCE,   // 匹配所有文件
                    TrueFileFilter.INSTANCE    // 匹配所有目录
            );
            for (File file : addonDirFile) {
                // 跳过根目录和商城
                if (file.equals(addonDir) || file.getName().equals("mall") || file.getName().equals("components")) {
                    continue;
                }
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    file.delete();
                }
            }

            // 处理pages.json
            File pages = new File(home + "/webroot/runtime/uni-app/src/pages.json");
            if (pages.exists()) {
                String content = FileUtils.readFileToString(pages, "UTF-8");
                Pattern pattern = Pattern.compile("// \\{\\{ PAGE_BEGAIN \\}\\}[\\s\\S]*?// \\{\\{ PAGE_END \\}\\}");
                Matcher matcher = pattern.matcher(content);

                Pattern mallPattern = Pattern.compile("(?s)(^\\s*//\\s*MALL_PAGE_BEGIN\\s*$.*?^\\s*//\\s*MALL_PAGE_END\\s*$)", Pattern.MULTILINE);
                Matcher mallMatcher = mallPattern.matcher(content);
                if (mallMatcher.find()) {
                    content = matcher.replaceAll("// {{ PAGE_BEGAIN }}\n"+ mallMatcher.group(1).trim() +"\n// {{ PAGE_END }}");
                } else {
                    content = matcher.replaceAll("// {{ PAGE_BEGAIN }}\n// {{ PAGE_END }}");
                }
                FileUtils.writeStringToFile(pages, content, "UTF-8");
            }

            // 处理 diy component 文件
            File diyComponentsFile = new File(home + "/webroot/resource/static/tmpl/diy-group.tpl");
            LinkedList<String> diyGroupImport = new LinkedList<String>();
            LinkedList<String> diyGroupComponent = new LinkedList<String>();
            File mallComponentsDir = new File(home + "/uni-app/src/addon/mall/components/diy");
            if (mallComponentsDir.exists()) {
                IOFileFilter vueFilter = new SuffixFileFilter("index.vue");
                IOFileFilter allFilesInVueDirs = FileFilterUtils.and(vueFilter, TrueFileFilter.INSTANCE);
                Collection<File> diyComponent = FileUtils.listFiles(mallComponentsDir, allFilesInVueDirs, TrueFileFilter.INSTANCE);
                for (File file : diyComponent) {
                    String name = new File(file.getParent()).getName();
                    String fileName = "diy-" + name;
                    String componentName = Arrays.stream(name.split("-")).map(s -> {
                        if (s == null || s.isEmpty()) return s;
                        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
                    }).collect(Collectors.joining(""));

                    String component = "            <template v-if=\"component.componentName == '" + componentName + "'\">\n";
                    component += "                <"+ fileName +" :component=\"component\" :global=\"data.global\" :index=\"index\" :scrollBool=\"diyGroup.componentsScrollBool."+ componentName +"\" @update:componentIsShow=\"component.componentIsShow = $event\" />\n";
                    component += "            </template>";

                    diyGroupComponent.add(component);
                    diyGroupImport.add("   import diy"+ componentName +" from '@/addon/mall/components/diy/" + name + "/index.vue';");
                }
            }
            String diyGroupContent = FileUtils.readFileToString(diyComponentsFile, "UTF-8").replace("{{ component }}", String.join("\n", diyGroupComponent)).replace("{{ componentImport }}", String.join("\n", diyGroupImport));
            FileUtils.writeStringToFile(new File(home + "/webroot/runtime/uni-app/src/addon/components/diy/group/index.vue"), diyGroupContent, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying uni-app ended <-");
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    private static void buildWebToRuntime(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying web start ->");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        try {
            File srcDir = new File(home + "/web");
            File destDir = new File(home + "/webroot/runtime/web");
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }

            String[] exclusionDir = {"node_modules", ".output", "nuxt", "dist"};
            FileUtils.copyDirectory(srcDir, destDir, createExclusionFilter(exclusionDir));

            // 移除插件
            File addonDir = new File(home + "/webroot/runtime/web/addon");
            Collection<File> addonDirFile = FileUtils.listFilesAndDirs(
                    addonDir,
                    TrueFileFilter.INSTANCE,   // 匹配所有文件
                    TrueFileFilter.INSTANCE    // 匹配所有目录
            );
            for (File file : addonDirFile) {
                // 跳过根目录和商城
                if (file.equals(addonDir) || file.getName().equals("mall") || file.getName().equals("components")) {
                    continue;
                }
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("error:" + e.getMessage());
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying web ended <-");
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * 排除目录
     *
     * @param dirs
     * @return
     */
    private static IOFileFilter createExclusionFilter(String[] dirs) {
        // 定义要排除的目录
        List<String> excludedDirs = Arrays.asList(dirs);

        // 创建一个过滤器来排除特定子目录
        IOFileFilter excludeSubDirsFilter = FileFilterUtils.trueFileFilter(); // 初始化为 false，表示过滤所有文件或目录

        // 循环创建过滤器
        for (String dirName : excludedDirs) {
            IOFileFilter tempFilter = FileFilterUtils.nameFileFilter(dirName);
            excludeSubDirsFilter = FileFilterUtils.and(excludeSubDirsFilter, FileFilterUtils.notFileFilter(tempFilter));
        }

        return excludeSubDirsFilter;
    }
}
