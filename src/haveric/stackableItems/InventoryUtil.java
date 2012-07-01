package haveric.stackableItems;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class InventoryUtil {

    private static StackableItems plugin;

    public static void init(StackableItems si) {
        plugin = si;
    }
    /*
    public static int getFreeSpaces(Player player, ItemStack itemToCheck, Inventory inventory){
        return getFreeSpaces(player, itemToCheck, inventory, 0, inventory.getContents().length);
    }

    public static int getFreeSpaces(Player player, ItemStack itemToCheck, int start, int end){
        return getFreeSpaces(player, itemToCheck, player.getInventory(), start, end);
    }
    */
    public static int getFreeSpaces(Player player, ItemStack itemToCheck) {
        return getFreeSpaces(player, itemToCheck, player.getInventory(), 0, 36);
    }

    public static int getFreeSpaces(Player player, ItemStack itemToCheck, Inventory inventory, int start, int end) {
        int free = 0;

        if (start < end && end <= inventory.getContents().length) {
            Material type = itemToCheck.getType();
            short durability = itemToCheck.getDurability();

            int maxAmount = SIItems.getItemMax(player, type, durability);
            if (maxAmount <= Config.ITEM_DEFAULT) {
                maxAmount = type.getMaxStackSize();
            }

            for (int i = start; i < end; i++) {
                ItemStack slot = inventory.getItem(i);

                if (slot == null) {
                    free += maxAmount;
                } else if (slot.getType() == type && slot.getDurability() == durability) {
                    boolean sameEnchants = slot.getEnchantments().equals(itemToCheck.getEnchantments());
                    boolean noEnchants = slot.getEnchantments() == null && itemToCheck.getEnchantments() == null;

                    if (sameEnchants || noEnchants) {
                        int freeInSlot = maxAmount - slot.getAmount();
                        if (freeInSlot > 0) {
                            free += freeInSlot;
                        }
                    }
                }
            }
        }
        return free;
    }

    /*
    public static void addItems(Player player, ItemStack itemToAdd, Inventory inventory){
        addItems(player, itemToAdd, inventory, 0, inventory.getContents().length);
    }

    public static void addItems(Player player, ItemStack itemToAdd, int start, int end){
        addItems(player, itemToAdd, player.getInventory(), start, end);
    }
    */

    public static void addItems(Player player, ItemStack itemToAdd) {
        addItems(player, itemToAdd, player.getInventory(), 0, 36);
    }
    // TODO: BROKEN
    public static void addItems(final Player player, final ItemStack itemToAdd, final Inventory inventory, final int start, final int end) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override public void run() {
                if (start < end && end <= inventory.getContents().length) {
                    Material type = itemToAdd.getType();
                    short durability = itemToAdd.getDurability();

                    int maxAmount = SIItems.getItemMax(player, type, durability);
                    if (maxAmount <= Config.ITEM_DEFAULT) {
                        maxAmount = type.getMaxStackSize();
                    }

                    int addAmount = itemToAdd.getAmount();
                    // Add to existing stacks
                    for (int i = start; i < end && addAmount > 0; i++) {
                        ItemStack slot = inventory.getItem(i);
                        if (slot != null) {
                            if (slot.getType() == type && slot.getDurability() == durability) {
                                boolean sameEnchants = slot.getEnchantments().equals(itemToAdd.getEnchantments());
                                boolean noEnchants = slot.getEnchantments() == null && itemToAdd.getEnchantments() == null;

                                if (sameEnchants || noEnchants) {
                                    int slotAmount = slot.getAmount();

                                    int canAdd = maxAmount - slotAmount;
                                    if (canAdd > 0) {
                                        if (addAmount <= canAdd) {
                                            slot.setAmount(slotAmount + addAmount);
                                            inventory.setItem(i, slot);
                                            addAmount = 0;
                                        } else if (addAmount <= maxAmount) {
                                            slot.setAmount(maxAmount);
                                            inventory.setItem(i, slot);
                                            addAmount -= canAdd;
                                        } else {
                                            slot.setAmount(maxAmount);
                                            inventory.setItem(i, slot);
                                            addAmount -= maxAmount;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add to empty slots
                    for (int i = start; i < end && addAmount > 0; i++) {
                        ItemStack slot = inventory.getItem(i);
                        if (slot == null) {
                            if (addAmount >= maxAmount) {
                                itemToAdd.setAmount(maxAmount);
                                inventory.setItem(i, itemToAdd.clone());
                                addAmount -= maxAmount;
                            } else if (addAmount > 0) {
                                itemToAdd.setAmount(addAmount);
                                inventory.setItem(i, itemToAdd.clone());
                                addAmount = 0;
                            }
                        }
                    }
                    if (addAmount > 0) {
                        ItemStack clone = itemToAdd.clone();
                        clone.setAmount(addAmount);
                        player.getWorld().dropItemNaturally(player.getLocation(), clone);
                    }
                }
            }
        });
    }

    public static void moveItems(Player player, ItemStack clicked, InventoryClickEvent event, int start, int end) {
        moveItems(player, clicked, event, player.getInventory(), start, end);
    }

    public static void moveItems(Player player, ItemStack clicked, InventoryClickEvent event, Inventory inventory) {
        moveItems(player, clicked, event, inventory, 0, inventory.getContents().length);
    }

    public static void moveItems(Player player, ItemStack clicked, InventoryClickEvent event, Inventory inventory, int start, int end) {
        event.setCancelled(true);
        ItemStack clone = clicked.clone();
        int free = getFreeSpaces(player, clone, inventory, start, end);

        int clickedAmount = clicked.getAmount();

        if (free >= clickedAmount) {
            addItems(player, clone, inventory, start, end);
            event.setCurrentItem(null);
        } else {
            int left = clickedAmount - free;
            if (left > 0) {
                clone.setAmount(free);
                addItems(player, clone, inventory, start, end);

                ItemStack clone2 = clicked.clone();
                clone2.setAmount(left);
                event.setCurrentItem(clone2);
            }
        }
    }

    public static int getCraftingAmount(Inventory inventory, Recipe recipe) {
        int amt = -1;

        //plugin.log.info("Item: " + recipe.getResult().getType());
        List<Recipe> recipes = plugin.getServer().getRecipesFor(recipe.getResult());
        for (Recipe rec : recipes) {
            if (rec instanceof ShapedRecipe) {
                //plugin.log.info("Shaped");
                ShapedRecipe shaped = (ShapedRecipe) rec;
                Map<Character, ItemStack> itemMap = shaped.getIngredientMap();
                String[] shape = shaped.getShape();
                int width = shape.length;
                int height = shape[0].length();

                int max = width * height;

                //plugin.log.info("AMT0: " + amt);
                amt = checkItemInInventory(inventory, itemMap.get('a'), amt);
                //plugin.log.info("AMT1: " + amt);
                if (max >= 2) {
                    amt = checkItemInInventory(inventory, itemMap.get('b'), amt);
                    //plugin.log.info("AMT2: " + amt);
                }
                if (max >= 3) {
                    amt = checkItemInInventory(inventory, itemMap.get('c'), amt);
                    //plugin.log.info("AMT3: " + amt);
                }
                if (max >= 4) {
                    amt = checkItemInInventory(inventory, itemMap.get('d'), amt);
                    //plugin.log.info("AMT4: " + amt);
                }
                if (max >= 5) {
                    amt = checkItemInInventory(inventory, itemMap.get('e'), amt);
                    //plugin.log.info("AMT5: " + amt);
                }
                if (max >= 6) {
                    amt = checkItemInInventory(inventory, itemMap.get('f'), amt);
                    //plugin.log.info("AMT6: " + amt);
                }
                if (max >= 7) {
                    amt = checkItemInInventory(inventory, itemMap.get('g'), amt);
                    //plugin.log.info("AMT7: " + amt);
                }
                if (max >= 8) {
                    amt = checkItemInInventory(inventory, itemMap.get('h'), amt);
                    //plugin.log.info("AMT8: " + amt);
                }
                if (max == 9) {
                    amt = checkItemInInventory(inventory, itemMap.get('i'), amt);
                    //plugin.log.info("AMT9: " + amt);
                }
            } else if (rec instanceof ShapelessRecipe) {
                ShapelessRecipe shapeless = (ShapelessRecipe) rec;
                //List<ItemStack> items = shapeless.getIngredientList();
                plugin.log.info("Please report the following line to github: ");
                plugin.log.info("Shapeless: " + shapeless.getResult());

            // TODO: Figure out if we need to handle FurnaceRecipes or not
            } else {

            }
        }

        if (amt == -1) {
            amt = 0;
        }
        return amt;
    }

    private static int checkItemInInventory(Inventory inventory, ItemStack ing, int amt) {
        if (ing != null) {
            int ingAmount = ing.getAmount();

            int holdingAmount = 0;

            //int[] invent = null;
            int length = inventory.getContents().length;
            for (int i = 1; i < length; i++) {
                ItemStack item = inventory.getItem(i);

                if (item != null) {
                    boolean sameEnchants = item.getEnchantments().equals(ing.getEnchantments());
                    boolean noEnchants = item.getEnchantments() == null && ing.getEnchantments() == null;

                    int dur = ing.getDurability();

                    //plugin.log.info("ingType: " + dur);
                    //plugin.log.info("itemType: " + item.getDurability());
                    if (ing.getType() == item.getType() && (dur == item.getDurability() || dur == -1) && (sameEnchants || noEnchants)) {
                        int temp = item.getAmount();
                        /*
                        if (temp > 0){
                            invent[i-1] = temp;
                        }
                        */
                        if (holdingAmount == 0 || holdingAmount > temp) {
                            holdingAmount = temp;
                        }
                    }
                }
            }

            // TODO: re-evaluate if the double is necessary
            int craftAmount = (int) Math.floor(holdingAmount / (double) ingAmount);
            //plugin.log.info("hold: " + holdingAmount);
            //plugin.log.info("ing: " + ingAmount);
            //plugin.log.info("Craft: " + craftAmount);
            if ((amt == -1 || amt == 0 || amt > craftAmount) && craftAmount > 0) {
                amt = craftAmount;
            }
        }
        return amt;
    }

    public static void removeFromCrafting(final CraftingInventory inventory, final int removeAmount) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override public void run() {
                int length = inventory.getContents().length;
                for (int i = 1; i < length; i++) {
                    ItemStack item = inventory.getItem(i);

                    if (item != null) {
                        int itemAmount = item.getAmount();
                        if (itemAmount == removeAmount) {
                            inventory.setItem(i, null);
                        } else {
                            int newAmount = itemAmount - removeAmount;
                            item.setAmount(newAmount);
                            inventory.setItem(i, item);
                        }
                    }
                }
            }
        });
    }
}