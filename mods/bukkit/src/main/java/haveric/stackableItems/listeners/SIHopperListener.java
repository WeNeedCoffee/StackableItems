package haveric.stackableItems.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.Comparator.Mode;
import org.bukkit.entity.Item;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import haveric.stackableItems.util.InventoryUtil;
import haveric.stackableItems.util.ItemUtil;
import haveric.stackableItems.util.SIItems;

public class SIHopperListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onRedstoneEvent(BlockRedstoneEvent event) {
		if (!event.getBlock().getType().equals(Material.COMPARATOR))
			return;
		Comparator c = (Comparator) event.getBlock().getBlockData();
		if (!c.getMode().equals(Mode.COMPARE))
			return;
		BlockFace face = c.getFacing();
		System.out.println(face);
		BlockState b = event.getBlock().getLocation().add(face.getModX(), face.getModY(), face.getModZ()).getBlock().getState();
		if (getInv(b) != null) {
			int e = calculateComparatorOutput(getInv(b));
			System.out.println(e);
			System.out.println(event.getOldCurrent());
			event.setNewCurrent(e);
			System.out.println(b);
		}
	}

	public static int floor(float f) {
		int i = (int) f;
		return f < (float) i ? i - 1 : i;
	}

	public static int calculateComparatorOutput(Inventory inventory) {
		if (inventory == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < inventory.getSize(); ++j) {
				ItemStack stack = inventory.getItem(j);
				if (stack != null) {
					int s = stack.getMaxStackSize();
					int e = SIItems.getInventoryMax(inventory.getLocation().getWorld().getName(), stack.getType(), stack.getDurability(), inventory.getType());
					f += (float) stack.getAmount() / (float) Math.min((e > 0 ? e : s), inventory.getMaxStackSize());
					++i;
				}
			}

			f /= (float) inventory.getSize();
			return floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}

	public Inventory getInv(BlockState b) {
		if (b instanceof Chest) {
			Chest block = (Chest) b;
			return block.getInventory();
		} else if (b instanceof DoubleChest) {
			DoubleChest block = (DoubleChest) b;
			return block.getInventory();
		} else if (b instanceof Hopper) {
			Hopper block = (Hopper) b;
			return block.getInventory();
		} else if (b instanceof HopperMinecart) {
			HopperMinecart block = (HopperMinecart) b;
			return block.getInventory();
		} else if (b instanceof StorageMinecart) {
			StorageMinecart block = (StorageMinecart) b;
			return block.getInventory();
		} else if (b instanceof BrewingStand) {
			BrewingStand block = (BrewingStand) b;
			return block.getInventory();
		} else if (b instanceof Dispenser) {
			Dispenser block = (Dispenser) b;
			return block.getInventory();
		} else if (b instanceof Dropper) {
			Dropper block = (Dropper) b;
			return block.getInventory();
		} else if (b instanceof Furnace) {
			Furnace block = (Furnace) b;
			return block.getInventory();
		} else if (b instanceof ShulkerBox) {
			ShulkerBox block = (ShulkerBox) b;
			return block.getInventory();
		}
		return null;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void hopperMove(InventoryMoveItemEvent event) {
		Inventory toInventory = event.getDestination();
		InventoryHolder holder = toInventory.getHolder();
		if (holder != null) {
			Location location = null;
			if (holder instanceof Chest) {
				Chest block = (Chest) holder;
				location = block.getLocation();
			} else if (holder instanceof DoubleChest) {
				DoubleChest block = (DoubleChest) holder;
				location = block.getLocation();
			} else if (holder instanceof Hopper) {
				Hopper block = (Hopper) holder;
				location = block.getLocation();
				if (location.getBlock().isBlockIndirectlyPowered() || location.getBlock().isBlockPowered()) {
					event.setCancelled(true);
					return;
				}
			} else if (holder instanceof HopperMinecart) {
				HopperMinecart block = (HopperMinecart) holder;
				location = block.getLocation();
				if (location.getBlock().isBlockIndirectlyPowered() || location.getBlock().isBlockPowered()) {
					event.setCancelled(true);
					return;
				}
			} else if (holder instanceof StorageMinecart) {
				StorageMinecart block = (StorageMinecart) holder;
				location = block.getLocation();
			} else if (holder instanceof Beacon) {
				Beacon block = (Beacon) holder;
				location = block.getLocation();
			} else if (holder instanceof BrewingStand) {
				BrewingStand block = (BrewingStand) holder;
				location = block.getLocation();
			} else if (holder instanceof Dispenser) {
				Dispenser block = (Dispenser) holder;
				location = block.getLocation();
			} else if (holder instanceof Dropper) {
				Dropper block = (Dropper) holder;
				location = block.getLocation();
			} else if (holder instanceof Furnace) {
				Furnace block = (Furnace) holder;
				location = block.getLocation();
			} else if (holder instanceof ShulkerBox) {
				ShulkerBox block = (ShulkerBox) holder;
				location = block.getLocation();
			}

			if (location != null) {
				ItemStack stack = event.getItem();
				int defaultMax = SIItems.getInventoryMax(location.getWorld().getName(), stack.getType(), stack.getDurability(), toInventory.getType());

				// Don't touch default or infinite items.
				if (!ItemUtil.isShulkerBox(stack.getType()))
					if (defaultMax == SIItems.ITEM_DEFAULT || defaultMax == SIItems.ITEM_INFINITE) {
						return;
					}

				if (defaultMax == 0 && !ItemUtil.isShulkerBox(stack.getType())) {
					event.setCancelled(true);
					return;
				}
				if (defaultMax > 0 || ItemUtil.isShulkerBox(stack.getType())) {
					if (!InventoryUtil.canVanillaMoveHopper(toInventory, stack)) {
						Inventory fromInventory = event.getSource();
						InventoryUtil.moveItemsFromHopper(location, stack.clone(), fromInventory, toInventory, defaultMax);

						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void hopperPickup(InventoryPickupItemEvent event) {
		Inventory inventory = event.getInventory();
		InventoryHolder holder = inventory.getHolder();
		String worldName = null;

		if (holder instanceof Hopper) {
			Hopper hopper = (Hopper) holder;
			worldName = hopper.getWorld().getName();
		} else if (holder instanceof HopperMinecart) {
			HopperMinecart hopper = (HopperMinecart) holder;
			worldName = hopper.getWorld().getName();
		}

		if (worldName != null && SIItems.isInventoryEnabled(worldName, inventory)) {
			Item item = event.getItem();
			ItemStack stack = item.getItemStack();

			int defaultMax = SIItems.getInventoryMax(item.getWorld().getName(), stack.getType(), stack.getDurability(), inventory.getType());

			// Don't touch default or infinite items.
			if (!ItemUtil.isShulkerBox(stack.getType()))
				if (defaultMax == SIItems.ITEM_DEFAULT || defaultMax == SIItems.ITEM_INFINITE) {
					return;
				}
			// Bounce the item up off the hopper
			if (defaultMax == 0) {
				item.setVelocity(new Vector((Math.random() * .5) - .25, .5, (Math.random() * .5) - .25));
				event.setCancelled(true);
			} else if (defaultMax > 0) {
				item.remove();
				InventoryUtil.addItems(item.getLocation(), stack, inventory, defaultMax, true);
				event.setCancelled(true);
			}
		}
	}

}
