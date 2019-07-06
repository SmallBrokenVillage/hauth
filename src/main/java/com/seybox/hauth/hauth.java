package com.seybox.hauth;

import com.seybox.hauth.util.dataStorageHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class hauth extends JavaPlugin implements Listener {
    private dataStorageHelper hauthHelper;

    private static final String hauthPassStorage = "hauthPassList.yml";

    private HashMap<String,String> loginStatus;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.hauthHelper = new dataStorageHelper(this);
        this.hauthHelper.createDataStorage(hauthPassStorage);
        this.loginStatus = new HashMap<String, String>();
        Bukkit.getPluginCommand("register").setExecutor(this);
        Bukkit.getPluginCommand("login").setExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        runTasks();
    }

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        loginStatus.put(player.getName(),"unlogined");
        String status = (String)hauthHelper.getDataStorage(hauthPassStorage,player.getName());
        if(status==null){
            player.sendMessage(ChatColor.RED + "[hauth] 使用 /register 密码 重复密码 注册！");
        } else {
            player.sendMessage(ChatColor.RED + "[hauth] 使用 /login 密码 登录！");
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        loginStatus.remove(player.getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = Bukkit.getPlayer(sender.getName());
            if (command.getName().equalsIgnoreCase("register")) {
                if(args.length!=2){
                    player.sendMessage(ChatColor.RED + "[hauth] 用法：/register 密码 重复密码");
                    return true;
                }
                String passwd = args[0];
                String repass = args[1];
                if(!passwd.equals(repass)){
                    player.sendMessage(ChatColor.RED + "[hauth] 两次输入密码不相同！");
                    return true;
                }
                if(passwd.equals(player.getName())){
                    player.sendMessage(ChatColor.RED + "[hauth] 不能用用户名做密码！");
                    return true;
                }
                hauthHelper.setDataStorage(hauthPassStorage,player.getName(),passwd);
                loginStatus.put(player.getName(),"logined");
                player.sendMessage(ChatColor.RED + "[hauth] 注册成功！并已成功登录！");
                return true;
            }
            if (command.getName().equalsIgnoreCase("login")) {
                if(args.length!=1){
                    return true;
                }
                String passwd = args[0];
                if(!passwd.equals((String)hauthHelper.getDataStorage(hauthPassStorage,player.getName()))){
                    player.sendMessage(ChatColor.RED + "[hauth] 密码错误或未注册！");
                    return true;
                } else {
                    loginStatus.put(player.getName(),"logined");
                    player.sendMessage(ChatColor.RED + "[hauth] 登录成功！");
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private void runTasks(){
        new BukkitRunnable(){
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()){
                    if(!loginStatus.get(player.getName()).equals("logined")){
                        loginStatus.put(player.getName(),"unlogined");
                        Bukkit.getServer()
                                .dispatchCommand(
                                        Bukkit.getConsoleSender(),
                                        "world tp "+ player.getName() + " main");
                        String status = (String)hauthHelper.getDataStorage(hauthPassStorage,player.getName());
                        if(status==null){
                            player.sendMessage(ChatColor.RED + "[hauth] 使用 /register 密码 重复密码 注册！");
                        } else {
                            player.sendMessage(ChatColor.RED + "[hauth] 使用 /login 密码 登录！");
                        }
                    }
                }
            }
        }.runTaskTimer(this,20,20);
    }
}
