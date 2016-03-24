package com.bluexin.saoui.resources;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tencao on 24/03/2016.
 */
public class ThemeLoader implements IResourcePack {

    public InputStream getInputStream(ResourceLocation rl)
            throws IOException
    {
        if (!resourceExists(rl)) {
            return null;
        }
        return new FileInputStream(new File("resources/" + rl.getResourceDomain(), rl.getResourcePath()));
    }

    public boolean resourceExists(ResourceLocation rl)
    {
        File fileRequested = new File("resources/" + rl.getResourceDomain(), rl.getResourcePath());
        if (fileRequested.exists()) {
            return true;
        }
        return false;
    }

    public Set getResourceDomains()
    {
        File folder = new File("resources");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String[] content = folder.list();

        HashSet<String> folders = new HashSet();
        for (String s : content)
        {
            File f = new File(folder, s);
            if ((f.exists()) && (f.isDirectory())) {
                folders.add(f.getName());
            }
        }
        return folders;
    }


    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_)
            throws IOException
    {
        return null;
    }


    public BufferedImage getPackImage()
            throws IOException
    {
        return null;
    }

    public String getPackName()
    {
        return "SAOUI Themes";
    }
}
