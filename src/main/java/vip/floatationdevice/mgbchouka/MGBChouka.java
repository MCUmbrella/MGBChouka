package vip.floatationdevice.mgbchouka;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import vip.floatationdevice.guilded4j.object.ChatMessage;
import vip.floatationdevice.mgbridge.GuildedCommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static vip.floatationdevice.mgbridge.BindManager.bindMap;
import static vip.floatationdevice.mgbridge.MGBridge.instance;

public final class MGBChouka extends JavaPlugin implements GuildedCommandExecutor
{
    ArrayList<Card> cards = new ArrayList<>();
    YamlConfiguration cfg;

    @Override
    public void onEnable()
    {
        // 加载配置文件
        File cfgFile = new File(getDataFolder(), "config.yml");
        if(!cfgFile.exists())
            saveDefaultConfig();
        cfg = YamlConfiguration.loadConfiguration(cfgFile);
        // 从cards节点读取卡片
        ConfigurationSection cardsSection = cfg.getConfigurationSection("cards");
        if(cardsSection == null)
            getLogger().warning("配置文件中没有cards节点，请检查配置文件");
        else
        {
            for(String key : cardsSection.getKeys(false))
            {
                ConfigurationSection cardSection = cardsSection.getConfigurationSection(key);
                if(cardSection == null)
                    getLogger().warning("cards节点下的" + key + "节点不是配置节点，请检查配置文件");
                else
                    cards.add(new Card(
                            cardSection.getString("name"),
                            cardSection.getString("description"),
                            cardSection.getString("imageUrl"),
                            cardSection.getString("rarity")
                    ));
            }
        }
        getLogger().info("配置文件读取完成。一共" + cards.size() + "张卡片");
        // 注册抽卡命令
        instance.getGEventListener().registerExecutor(this);
        getLogger().info("抽卡命令已注册");
    }

    @Override
    public void onDisable()
    {
        instance.getGEventListener().unregisterExecutor(getCommandName());
        getLogger().info("抽卡命令已取消注册");
    }

    @Override
    public String getCommandName()
    {
        return "chouka";
    }

    @Override
    public boolean execute(ChatMessage chatMessage, String[] args)
    {
        if(cards.size() != 0 && bindMap.containsKey(chatMessage.getCreatorId()))
        {
            getLogger().info("Guilded用户（ID：" + chatMessage.getCreatorId() + "）发送了抽卡命令");
            // 随机抽卡并且回复给Guilded用户
            Card card = cards.get(new Random().nextInt(cards.size() - 1));
            getLogger().info("抽到了" + card.getName());
            instance.sendGuildedMsg(
                    "`恭喜你获得：`\n" +
                            "**====== " + card.getName() + " ======**\n" +
                            "“" + card.getRarity() + "”角色\n" +
                            (card.getDescription().equals("") ? "" : ("_“" + card.getDescription() + "”_")),
                    chatMessage.getId()
            );
            // Guilded API目前不支持同时发送文字和图片，所以只能单独发送图片
            if(card.getImageUrl() != null && !card.getImageUrl().equals(""))
                instance.sendGuildedMsg("![](" + card.getImageUrl() + ")", chatMessage.getId());
            return true;
        }
        else return false;
    }
}