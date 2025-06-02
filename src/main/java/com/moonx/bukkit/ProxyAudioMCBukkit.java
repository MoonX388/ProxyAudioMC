package com.moonx.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProxyAudioMCBukkit extends JavaPlugin {

    private File addonsFolder;

    @Override
    public void onEnable() {
        // Membuat folder addons jika belum ada
        addonsFolder = new File(getDataFolder(), "addons");
        if (!addonsFolder.exists()) {
            addonsFolder.mkdirs();
        }

        // Load dan otomatis register addon
        loadAndRegisterAddons();
    }

    private void loadAndRegisterAddons() {
        File[] addonFiles = addonsFolder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (addonFiles != null) {
            for (File file : addonFiles) {
                try {
                    // Baca mainClass dari addon.yml di dalam jar
                    String mainClassName = getMainClassNameFromJar(file);
                    if (mainClassName == null) {
                        getLogger().warning("Tidak menemukan mainClass di " + file.getName());
                        continue;
                    }

                    // Load class utama addon
                    URL jarUrl = file.toURI().toURL();
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, getClass().getClassLoader());

                    Class<?> cls = classLoader.loadClass(mainClassName);

                    // Cari method register(String, JavaPlugin)
                    Method registerMethod = cls.getMethod("register", String.class, JavaPlugin.class);
                    Object instance = cls.getDeclaredConstructor().newInstance();

                    String addonName = file.getName().substring(0, file.getName().length() - 4);
                    // Panggil method register
                    registerMethod.invoke(instance, addonName, this);

                    getLogger().info("Addon '" + addonName + "' berhasil dimuat dan didaftarkan.");

                } catch (Exception e) {
                    getLogger().severe("Gagal memuat addon " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    // Membaca mainClass dari addon.yml di dalam jar
    private String getMainClassNameFromJar(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            // Cari file addon.yml di dalam jar
            JarEntry entry = jar.getJarEntry("addon.yml");
            if (entry == null) {
                getLogger().warning("Tidak ditemukan addon.yml di " + jarFile.getName());
                return null;
            }

            try (InputStream is = jar.getInputStream(entry)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("mainClass:")) {
                        // Ambil nilai setelah colon
                        String mainClass = line.split(":", 2)[1].trim();
                        return mainClass;
                    }
                }
            }
        } catch (IOException e) {
            getLogger().severe("Error membaca " + jarFile.getName() + ": " + e.getMessage());
        }
        return null;
    }
}