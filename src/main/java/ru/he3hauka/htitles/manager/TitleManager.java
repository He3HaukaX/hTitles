package ru.he3hauka.htitles.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;

public class TitleManager {
    private final LuckPerms luckPerms;

    public TitleManager(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    public void setSuffix(Player player, String suffix) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        user.data().clear(node -> node instanceof SuffixNode);
        SuffixNode node = SuffixNode.builder(suffix, 100).build();
        user.data().add(node);
        luckPerms.getUserManager().saveUser(user);
    }
}
