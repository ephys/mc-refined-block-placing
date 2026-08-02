package be.ephys.refinedblockplacing;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
  modid = Mod.MODID,
  value = net.minecraftforge.api.distmarker.Dist.CLIENT
)
final public class Client {
  private static BlockPos lastTargetPos;

  private static Direction lastTargetSide;

  /**
   * If the player just interacted with a block, and that interaction causes the tracehit to change target
   * (such as opening a door), the player's delay will reset the very next tick even though it's not the intention of the player.
   * <br />
   * To fix this, after a block is successfully interacted with, we reset to the new coordinates again.
   */
  private static boolean skipNextDelayReset = false;

  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.START) {
      Client.onClientTickStart();
    }
  }

  @SubscribeEvent
  public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    if (!event.getSide().isClient()) {
      return;
    }

    skipNextDelayReset = true;
  }

  private static void onClientTickStart() {
    Minecraft client = Minecraft.getInstance();

    HitResult hover = client.hitResult;
    if (hover == null || hover.getType() != HitResult.Type.BLOCK) {
      return;
    }

    BlockHitResult hit = (BlockHitResult) hover;
    Direction side = hit.getDirection();
    BlockPos pos = hit.getBlockPos();

    if (skipNextDelayReset) {
      lastTargetPos = pos;
      lastTargetSide = side;
      skipNextDelayReset = false;
    }

    if (client.rightClickDelay > 0 && !pos.equals(lastTargetPos) && (lastTargetPos == null || !pos.equals(lastTargetPos.relative(lastTargetSide)))) {
      client.rightClickDelay = 0;
    } else if (pos.equals(lastTargetPos) && side == lastTargetSide) {
      client.rightClickDelay = 4;
    }

    lastTargetPos = pos;
    lastTargetSide = side;
  }
}
