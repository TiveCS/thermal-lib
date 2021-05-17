package io.github.tivecs.thermallib.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    public static String RAW_SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    private JavaPlugin plugin;
    private String resourceId;

    public UpdateChecker(@Nonnull JavaPlugin plugin, @Nonnull String resourceId){
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                InputStream inputStream = new URL(RAW_SPIGOT_URL + getResourceId()).openStream();
                Scanner scanner = new Scanner(inputStream);
                String space = "";
                StringBuilder builder = new StringBuilder();
                while (scanner.hasNext()) {
                    builder.append(space).append(scanner.next());
                    space = " ";
                }

                consumer.accept(builder.toString());
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public String getResourceId() {
        return resourceId;
    }
}
