package io.github.maths.shiny.utils;

import io.github.maths.shiny.Shiny;
import org.bukkit.inventory.*;
import org.yaml.snakeyaml.external.biz.base64Coder.*;
import org.bukkit.util.io.*;
import java.io.*;
import org.bukkit.*;

public final class BukkitUtils
{
    public static final String SERVER_VERSION;
    public static final int SERVER_VERSION_INT;
    
    public static String getLocation(final Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ();
    }
    
    public static String serializeLocation(final Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ();
    }
    
    public static Location deserializeLocation(final String data) {
        final String[] a = data.split(", ");
        final Location location = new Location(Shiny.getInstance().getServer().getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]));
        return location;
    }
    
    public static String serializeItemStackArray(final ItemStack[] items) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream((OutputStream)outputStream);
            dataOutput.writeInt(items.length);
            for (final ItemStack item : items) {
                dataOutput.writeObject((Object)item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static ItemStack[] deserializeItemStackArray(final String data) {
        if (data == null) {
            return new ItemStack[0];
        }
        if (data.equals("")) {
            return new ItemStack[0];
        }
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            final BukkitObjectInputStream dataInput = new BukkitObjectInputStream((InputStream)inputStream);
            final ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; ++i) {
                items[i] = (ItemStack)dataInput.readObject();
            }
            dataInput.close();
            return items;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }
    
    public static ItemStack deserializeItemStack(final String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            final InputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            try {
                final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                try {
                    final ItemStack itemStack2;
                    final ItemStack itemStack = itemStack2 = (ItemStack)dataInput.readObject();
                    dataInput.close();
                    inputStream.close();
                    return itemStack2;
                }
                catch (Throwable t) {
                    try {
                        dataInput.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Throwable t2) {
                try {
                    inputStream.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (IOException | ClassNotFoundException ex2) {
            final Exception ex = null;
            final Exception e = ex;
            e.printStackTrace();
            return null;
        }
    }
    
    public static String serializeItemStack(final ItemStack itemStack) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream((OutputStream)outputStream);
                try {
                    dataOutput.writeObject((Object)itemStack);
                    dataOutput.close();
                    final String encodeLines = Base64Coder.encodeLines(outputStream.toByteArray());
                    dataOutput.close();
                    outputStream.close();
                    return encodeLines;
                }
                catch (Throwable t) {
                    try {
                        dataOutput.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Throwable t2) {
                try {
                    outputStream.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private BukkitUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    static {
        SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
        SERVER_VERSION_INT = Integer.parseInt(BukkitUtils.SERVER_VERSION.replace("1_", "").replaceAll("_R\\d", ""));
    }
}
