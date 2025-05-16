package ru.he3hauka.htitles.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.he3hauka.htitles.actions.ActionExecutor;
import ru.he3hauka.htitles.config.Config;
import ru.he3hauka.htitles.manager.TitleManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ru.he3hauka.htitles.utils.HexSupport.formatter;

public class TitleHandler implements CommandExecutor {
    private final TitleManager titleManager;
    private final ActionExecutor actionExecutor;
    private final Config config;
    private final Map<UUID, String> pendingTitles = new HashMap<>();

    public TitleHandler(TitleManager titleManager,
                        ActionExecutor actionExecutor,
                        Config config) {
        this.titleManager = titleManager;
        this.actionExecutor = actionExecutor;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("htitle.use") && !player.isOp()) {
            actionExecutor.execute(player, config.actions_permission, "N/A", player.getLocation());
            return true;
        }

        if (args.length == 0) {
            actionExecutor.execute(player, config.actions_help, "N/A", player.getLocation());
            return true;
        }

        if (args[0].equalsIgnoreCase("confirm")) {
            if (pendingTitles.containsKey(player.getUniqueId())) {
                String title = pendingTitles.get(player.getUniqueId());
                titleManager.setSuffix(player, title);
                pendingTitles.remove(player.getUniqueId());
                actionExecutor.execute(player, config.actions_finish, "N/A", player.getLocation());
            } else {
                actionExecutor.execute(player, config.actions_confirm_null, "N/A", player.getLocation());
            }
            return true;
        }

        String rawTitle = String.join(" ", args);

        String cleanTitle = rawTitle
                .replaceAll("§[0-9a-fk-or]", "")
                .replaceAll("&#([a-fA-F\\d]{6})", "")
                .replaceAll("#([a-fA-F\\d]{6})", "");

        int length = cleanTitle.codePointCount(0, cleanTitle.length());

        if (length > config.settings_max_size + 1) {
            actionExecutor.execute(player, config.actions_max_size, cleanTitle, player.getLocation());
            return true;
        }

        String formattedTitle = formatter(rawTitle);

        pendingTitles.put(player.getUniqueId(), formattedTitle);
        actionExecutor.execute(player, config.actions_confirm, formattedTitle, player.getLocation());

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (pendingTitles.remove(player.getUniqueId()) != null) {
                            actionExecutor.execute(player, config.actions_confirm_expired, formattedTitle, player.getLocation());
                        }
                    }
                },
                config.settings_delay
        );

        return true;
    }
}