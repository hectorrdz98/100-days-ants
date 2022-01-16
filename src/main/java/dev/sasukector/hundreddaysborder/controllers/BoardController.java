package dev.sasukector.hundreddaysborder.controllers;

import dev.sasukector.hundreddaysborder.HundredDaysBorder;
import dev.sasukector.hundreddaysborder.helpers.FastBoard;
import dev.sasukector.hundreddaysborder.helpers.ServerUtilities;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class BoardController {

    private static BoardController instance = null;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private @Setter @Getter boolean hideDays;

    public static BoardController getInstance() {
        if (instance == null) {
            instance = new BoardController();
        }
        return instance;
    }

    public BoardController() {
        Bukkit.getScheduler().runTaskTimer(HundredDaysBorder.getInstance(), this::updateBoards, 0L, 20L);
        this.hideDays = false;
    }

    public void newPlayerBoard(Player player) {
        FastBoard board = new FastBoard(player);
        this.boards.put(player.getUniqueId(), board);
    }

    public void removePlayerBoard(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void updateBoards() {
        boards.forEach((uuid, board) -> {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;

            board.updateTitle("§9§l100 días");

            List<String> lines = new ArrayList<>();
            lines.add("");

            World overworld = ServerUtilities.getWorld("overworld");
            if (overworld != null && !hideDays) {
                lines.add("Día: §d" + (overworld.getFullTime() / 24000));
                lines.add("Borde: §d" + String.format("%.0f", (overworld.getWorldBorder().getSize())));
            }

            double hours = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20.0/ 60.0 / 60.0;
            lines.add("Jugado: §d" + String.format("%.2f", hours) + " h");

            lines.add("");
            lines.add("Online: §6" + Bukkit.getOnlinePlayers().size());
            if (player.isOp() && !player.getName().equals("Conterstine")) {
                lines.add("TPS: §6" + String.format("%.2f", Bukkit.getTPS()[0]));
            }

            board.updateLines(lines);
        });
    }

}
