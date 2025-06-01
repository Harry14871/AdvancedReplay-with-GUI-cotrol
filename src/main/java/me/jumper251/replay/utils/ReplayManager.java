package me.jumper251.replay.utils;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.commands.replay.ReplayCommand;
import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.listener.ReplayListener;
import me.jumper251.replay.replaysystem.Replay;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplayManager {

    // 原有錄影實例暫存
    public static HashMap<String, Replay> activeReplays = new HashMap<>();

    // 新增：記錄每位玩家目前錄製的 replay 檔名
    private static final Map<UUID, String> sessionMap = new HashMap<>();

    // 註冊所有事件與指令
    public static void register() {
        registerEvents();
        registerCommands();

        if (ConfigManager.RECORD_STARTUP) {
            ReplayAPI.getInstance().recordReplay(null, Bukkit.getConsoleSender());
        }

        Bukkit.getScheduler().runTaskAsynchronously(ReplaySystem.getInstance(), ReplayManager::delayedInit);
    }

    private static void registerEvents() {
        new ReplayListener().register();
    }

    private static void registerCommands() {
        ReplaySystem.getInstance().getCommand("replay").setExecutor(new ReplayCommand());
    }

    private static void delayedInit() {
        if (VersionUtil.isAbove(VersionUtil.VersionEnum.V1_21)) {
            ProtocolLibUtil.prepare();
        }
    }

    // ========== Session 功能 ==========

    // 記錄一位玩家的錄影 session（uuid 對應 replay 檔名）
    public static void registerSession(UUID playerId, String fileName) {
        sessionMap.put(playerId, fileName);
    }

    // 取得該玩家目前錄製中的 replay 檔名
    public static String getSessionFile(UUID playerId) {
        return sessionMap.get(playerId);
    }

    // 停止錄影時清除 session
    public static void removeSession(UUID playerId) {
        sessionMap.remove(playerId);
    }
}
