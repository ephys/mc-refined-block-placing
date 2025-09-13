package be.ephys.refinedblockplacing;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
  modid = Mod.MODID,
  value = net.minecraftforge.api.distmarker.Dist.CLIENT
)
final public class Client {
  private static BlockPos lastTargetPos;

  private static Direction lastTargetSide;

  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.START) {
      Client.onClientTickStart();
    }
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

    if (client.rightClickDelay > 0 && !pos.equals(lastTargetPos) && (lastTargetPos == null || !pos.equals(lastTargetPos.relative(lastTargetSide)))) {
      client.rightClickDelay = 0;
    } else if (pos.equals(lastTargetPos) && side == lastTargetSide) {
      client.rightClickDelay = 4;
    }

    lastTargetPos = pos;
    lastTargetSide = side;
  }
}
