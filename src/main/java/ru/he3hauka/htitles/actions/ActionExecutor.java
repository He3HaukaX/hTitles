package ru.he3hauka.htitles.actions;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.he3hauka.htitles.utils.HexSupport.format;

public class ActionExecutor {

    private static final Pattern HOVER_PATTERN = Pattern.compile("\\{HoverText:cmd (.*?), text: (.*?)}");
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();
    private final Map<UUID, BossBar> activeBossBars = new HashMap<>();

    public void execute(Player player,
                        List<String> actions,
                        String title,
                        Location location) {
        for (String action : actions) {
            if (!action.contains(" ")) continue;

            String type = action.substring(0, action.indexOf(" "));
            String content = action.substring(type.length()).trim();

            switch (type) {
                case "[Message]" -> sendMessage(player, content, title);
                case "[Sound]" -> playSound(player, content);
                case "[Title]" -> sendTitle(player, content, title);
                case "[Console]" -> runConsoleCommand(player, content, title);
                case "[Broadcast]" -> broadcastMessage(player, content, title);
                case "[Actionbar]" -> sendActionBar(player, content, title);
                case "[Bossbar]" -> sendBossBar(player, content, title);
                case "[Effect]" -> playEffect(location, content);
                case "[Player]" -> player.performCommand(content);
                default -> Bukkit.getLogger().severe("Unknown actionType: " + type);
            }
        }
    }

    private void sendMessage(Player player,
                             String raw,
                             String title) {
        if (player == null) return;
        String message = formatWithPlaceholders(raw, player, title);

        Matcher matcher = HOVER_PATTERN.matcher(message);
        if (matcher.find()) {
            String command = matcher.group(1);
            String hover = matcher.group(2);
            String mainText = message.substring(0, matcher.start()) + message.substring(matcher.end());

            TextComponent component = SERIALIZER.deserialize(mainText)
                    .hoverEvent(HoverEvent.showText(SERIALIZER.deserialize(formatWithPlaceholders(hover, player, title))))
                    .clickEvent(ClickEvent.runCommand(command));

            player.sendMessage(component);
        } else {
            player.sendMessage(SERIALIZER.deserialize(message));
        }
    }

    private void broadcastMessage(Player source,
                                  String raw,
                                  String title) {
        String message = formatWithPlaceholders(raw, source, title);

        Matcher matcher = HOVER_PATTERN.matcher(message);
        if (matcher.find()) {
            String command = matcher.group(1);
            String hover = matcher.group(2);
            String mainText = message.substring(0, matcher.start()) + message.substring(matcher.end());

            TextComponent component = SERIALIZER.deserialize(mainText)
                    .hoverEvent(HoverEvent.showText(SERIALIZER.deserialize(formatWithPlaceholders(hover, source, title))))
                    .clickEvent(ClickEvent.runCommand(command));

            Bukkit.broadcast(component);
        } else {
            Component component = SERIALIZER.deserialize(message);
            Bukkit.broadcast(component);
        }
    }

    private void playSound(Player player,
                           String content) {
        if (player == null) return;
        try {
            String[] parts = content.split(":");
            Sound sound = Sound.valueOf(parts[0].trim().toUpperCase());
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1f;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1f;
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Unknown sound or sound parameters!");
        }
    }

    private void sendTitle(Player player,
                           String raw,
                           String title2) {
        if (player == null) return;
        String formatted = formatWithPlaceholders(raw, player, title2);
        String[] parts = formatted.split("&&");
        Component title = SERIALIZER.deserialize(parts.length > 0 ? parts[0].trim() : "");
        Component subtitle = SERIALIZER.deserialize(parts.length > 1 ? parts[1].trim() : "");

        player.showTitle(Title.title(
                title,
                subtitle,
                Title.Times.of(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(1000))
        ));
    }

    private void playEffect(Location location,
                            String effect) {
        if (location == null || effect == null || effect.isEmpty()) {
            Bukkit.getLogger().warning("Location or effect name is invalid!");
            return;
        }

        try {
            Effect effectEnum = Effect.valueOf(effect.toUpperCase());
            location.getWorld().playEffect(location, effectEnum, 0);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Unknown effect: " + effect);
        }
    }

    private void runConsoleCommand(Player player,
                                   String command,
                                   String title) {
        String parsed = formatWithPlaceholders(command, player, title);
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, parsed);
    }

    private void sendActionBar(Player player,
                               String raw,
                               String title) {
        if (player == null) return;
        String message = formatWithPlaceholders(raw, player, title);
        Component component = SERIALIZER.deserialize(message);
        player.sendActionBar(component);
    }

    private void sendBossBar(Player player,
                             String raw,
                             String title) {
        if (player == null) return;
        String formatted = formatWithPlaceholders(raw, player, title);
        String[] parts = formatted.split(":", 5);
        if (parts.length < 2) {
            Bukkit.getLogger().warning("Format is incorrect. Expected: Text:Duration:Color:Style");
            return;
        }
        String text = parts[0].trim();
        long durationTicks = Long.parseLong(parts[1].trim());
        BossBar.Color color = parts.length > 2 ? BossBar.Color.valueOf(parts[2].trim().toUpperCase()) : BossBar.Color.PURPLE;
        BossBar.Overlay overlay = parts.length > 3 ? BossBar.Overlay.valueOf(parts[3].trim().toUpperCase()) : BossBar.Overlay.PROGRESS;
        float progress = parts.length > 4 ? Float.parseFloat(parts[4].trim()) : 1.0f;

        BossBar bossBar = BossBar.bossBar(SERIALIZER.deserialize(text), progress, color, overlay);
        if (activeBossBars.containsKey(player.getUniqueId())) {
            player.hideBossBar(activeBossBars.get(player.getUniqueId()));
        }
        activeBossBars.put(player.getUniqueId(), bossBar);
        player.showBossBar(bossBar);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("hTitles"), () -> {
            player.hideBossBar(bossBar);
            activeBossBars.remove(player.getUniqueId());
        }, durationTicks);
    }

    private String formatWithPlaceholders(String text,
                                          Player player,
                                          String title) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        text = text.replace("%player%", player.getName());
        text = text.replace("%title%", title != null ? title : "");
        text = format(text);
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}