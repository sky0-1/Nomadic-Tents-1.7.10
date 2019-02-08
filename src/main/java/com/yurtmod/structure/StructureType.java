package com.yurtmod.structure;

import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.items.ItemTent;
import com.yurtmod.main.Config;
import com.yurtmod.main.Content;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public enum StructureType {
	YURT_SMALL(Size.SMALL, 2), YURT_MEDIUM(Size.MEDIUM, 3), YURT_LARGE(Size.LARGE, 4), TEPEE_SMALL(Size.SMALL, 2),
	TEPEE_MEDIUM(Size.MEDIUM, 3), TEPEE_LARGE(Size.LARGE, 4), BEDOUIN_SMALL(Size.SMALL, 2),
	BEDOUIN_MEDIUM(Size.MEDIUM, 3), BEDOUIN_LARGE(Size.LARGE, 4);

	private final StructureType.Size size;
	private final int doorOffsetZ;
	public final String registryName;

	private StructureType(StructureType.Size s, int doorZ) {
		this.size = s;
		this.doorOffsetZ = doorZ;
		this.registryName = this.toString().toLowerCase();
	}

	/** @return the StructureType.Size value for this structure **/
	public StructureType.Size getSize() {
		return this.size;
	}

	/** @return square width of the structure **/
	public int getSqWidth() {
		return this.size.getSquareWidth();
	}

	/**
	 * @return The door is this number of blocks right from the front-left corner
	 *         block
	 **/
	public int getDoorPosition() {
		// on z-axis in Tent Dimension
		return doorOffsetZ;
	}

	public ItemStack getDropStack() {
		return new ItemStack(Content.ITEM_TENT, 1, this.ordinal());
	}
	
	public ItemStack getDropStack(TileEntityTentDoor te) {
		return getDropStack(te.getOffsetX(), te.getOffsetZ(), te.getPrevStructureType().ordinal(), te.getStructureType().ordinal());
	}

	public ItemStack getDropStack(int tagChunkX, int tagChunkZ, int prevStructure, int tentType) {
		ItemStack stack = this.getDropStack();
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger(ItemTent.OFFSET_X, tagChunkX);
		stack.getTagCompound().setInteger(ItemTent.OFFSET_Z, tagChunkZ);
		stack.getTagCompound().setInteger(ItemTent.PREV_TENT_TYPE, prevStructure);
		return stack;
	}

	public static void applyToTileEntity(EntityPlayer player, ItemStack stack, TileEntityTentDoor te) {
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(ItemTent.OFFSET_X)) {
			System.out.println("[StructureType] ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}

		int offsetx = stack.getTagCompound().getInteger(ItemTent.OFFSET_X);
		int offsetz = stack.getTagCompound().getInteger(ItemTent.OFFSET_Z);
		int prevStructure = stack.getTagCompound().getInteger(ItemTent.PREV_TENT_TYPE);
		int curStructure = stack.getItemDamage();
		te.setPrevStructureType(get(prevStructure));
		te.setStructureType(get(curStructure));
		te.setOffsetX(offsetx);
		te.setOffsetZ(offsetz);
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
	}

	public StructureBase getNewStructure() {
		switch (this) {
		case BEDOUIN_LARGE:
		case BEDOUIN_MEDIUM:
		case BEDOUIN_SMALL:
			return new StructureBedouin(this);
		case TEPEE_LARGE:
		case TEPEE_MEDIUM:
		case TEPEE_SMALL:
			return new StructureTepee(this);
		case YURT_LARGE:
		case YURT_MEDIUM:
		case YURT_SMALL:
			return new StructureYurt(this);
		}
		return null;
	}

	public Block getDoorBlock() {
		switch (this) {
		case YURT_SMALL:
			return Content.yurtDoorSmall;
		case YURT_MEDIUM:
			return Content.yurtDoorMed;
		case YURT_LARGE:
			return Content.yurtDoorLarge;
		case TEPEE_SMALL:
			return Content.tepeeDoorSmall;
		case TEPEE_MEDIUM:
			return Content.tepeeDoorMed;
		case TEPEE_LARGE:
			return Content.tepeeDoorLarge;
		case BEDOUIN_SMALL:
			return Content.bedDoorSmall;
		case BEDOUIN_MEDIUM:
			return Content.bedDoorMed;
		case BEDOUIN_LARGE:
			return Content.bedDoorLarge;
		}
		return null;
	}

	public Block getWallBlock(int dimID) {
		switch (this) {
		case YURT_SMALL:
		case YURT_MEDIUM:
		case YURT_LARGE:
			return dimID == Config.DIMENSION_ID ? Content.YURT_WALL_INNER : Content.YURT_WALL_OUTER;
		case TEPEE_SMALL:
		case TEPEE_MEDIUM:
		case TEPEE_LARGE:
			return Content.TEPEE_WALL;
		case BEDOUIN_SMALL:
		case BEDOUIN_MEDIUM:
		case BEDOUIN_LARGE:
			return Content.BEDOUIN_WALL;
		}
		return null;
	}

	public Block getRoofBlock() {
		switch (this) {
		case YURT_SMALL:
		case YURT_MEDIUM:
		case YURT_LARGE:
			return Content.YURT_ROOF;
		case TEPEE_SMALL:
		case TEPEE_MEDIUM:
		case TEPEE_LARGE:
			return Content.TEPEE_WALL;
		case BEDOUIN_SMALL:
		case BEDOUIN_MEDIUM:
		case BEDOUIN_LARGE:
			return Content.BEDOUIN_ROOF;
		}
		return null;
	}

	public Block getFrameBlock(boolean isRoof) {
		switch (this) {
		case YURT_SMALL:
		case YURT_MEDIUM:
		case YURT_LARGE:
			return isRoof ? Content.FRAME_YURT_ROOF : Content.FRAME_YURT_WALL;
		case TEPEE_SMALL:
		case TEPEE_MEDIUM:
		case TEPEE_LARGE:
			return Content.tepeeFrame;
		case BEDOUIN_SMALL:
		case BEDOUIN_MEDIUM:
		case BEDOUIN_LARGE:
			return isRoof ? Content.FRAME_BEDOUIN_ROOF : Content.FRAME_BEDOUIN_WALL;
		}
		return null;
	}

	/** @return the Z-offset of this structure type in the Tent Dimension **/
	public int getTagOffsetZ() {
		return (this.ordinal() / 3) * 2;
	}

	public EnumChatFormatting getTooltipColor() {
		return this.size.getTooltipColor();
	}

	public static String getName(ItemStack stack) {
		return getName(stack.getItemDamage());
	}

	public static String getName(int metadata) {
		return get(metadata).registryName;
	}

	public static StructureType get(int meta) {
		return StructureType.values()[meta % StructureType.values().length];
	}

	public static enum Size {
		SMALL(5), MEDIUM(7), LARGE(9);
		// Might be implemented later:
		// HUGE(11),
		// GIANT(13),
		// MEGA(15);
		private final int squareWidth;

		private Size(int sq) {
			this.squareWidth = sq;
		}

		public int getSquareWidth() {
			return this.squareWidth;
		}

		public EnumChatFormatting getTooltipColor() {
			switch (this) {
			case SMALL:
				return EnumChatFormatting.RED;
			case MEDIUM:
				return EnumChatFormatting.BLUE;
			case LARGE:
				return EnumChatFormatting.GREEN;
			// the following are not used now but might be later
			// case HUGE: return TextFormatting.YELLOW;
			// case GIANT: return TextFormatting.AQUA;
			// case MEGA: return TextFormatting.LIGHT_PURPLE;
			}
			return EnumChatFormatting.GRAY;
		}
	}
}