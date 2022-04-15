package vip.floatationdevice.mgbchouka;

public class Card
{
    private final String name;
    private final String description;
    private final String imageUrl;
    private final String rarity;

    public Card(String name, String description, String imageUrl, String rarity)
    {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rarity = rarity;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getRarity()
    {
        return rarity;
    }
}