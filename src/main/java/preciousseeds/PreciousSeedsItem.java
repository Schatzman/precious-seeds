package preciousseeds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Handles "stuck mature crop" replanting by allowing the seed use action to reset a mature
 * precious crop back to age 0 when the crop tile still sits above vanilla farmland.
 */
public final class PreciousSeedsItem extends ItemNameBlockItem {
	public PreciousSeedsItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult vanilla = super.useOn(context);
		if (vanilla.consumesAction()) {
			return vanilla;
		}

		BlockPos farmlandPos = findFarmlandPos(context);
		if (farmlandPos == null) {
			return vanilla;
		}

		Level level = context.getLevel();
		BlockPos cropPos = farmlandPos.above();
		BlockState cropState = level.getBlockState(cropPos);
		Block cropBlock = getBlock();
		if (!cropState.is(cropBlock)) {
			return vanilla;
		}

		CropBlock crop = (CropBlock) cropBlock;
		if (!crop.isMaxAge(cropState)) {
			return vanilla;
		}

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		boolean replaced = level.setBlock(cropPos, crop.defaultBlockState(), Block.UPDATE_ALL);
		if (!replaced) {
			return InteractionResult.PASS;
		}

		var player = context.getPlayer();
		if (player == null || !player.getAbilities().instabuild) {
			context.getItemInHand().shrink(1);
		}
		PreciousSeedsMod.LOGGER.info(
				"Replant fallback replaced mature crop at {} (state had remained {}).", cropPos, cropState);
		return InteractionResult.CONSUME;
	}

	private BlockPos findFarmlandPos(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState clickedState = level.getBlockState(clickedPos);
		if (clickedState.is(Blocks.FARMLAND)) {
			return clickedPos;
		}

		if (clickedState.is(getBlock()) && level.getBlockState(clickedPos.below()).is(Blocks.FARMLAND)) {
			return clickedPos.below();
		}
		return null;
	}
}
