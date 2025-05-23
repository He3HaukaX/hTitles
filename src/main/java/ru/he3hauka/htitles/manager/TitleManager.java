package ru.he3hauka.htitles.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;
import ru.he3hauka.htitles.config.Config;

public class TitleManager {
    private final LuckPerms luckPerms;
    private final Config config;
    public TitleManager(LuckPerms luckPerms,
                        Config config) {
        this.luckPerms = luckPerms;
        this.config = config;
    }

    public void setSuffix(Player player, String suffix) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        user.data().clear(node -> node instanceof SuffixNode);
        SuffixNode node = SuffixNode.builder(suffix, config.settings_priority).build();
        user.data().add(node);
        luckPerms.getUserManager().saveUser(user);
    }
}
