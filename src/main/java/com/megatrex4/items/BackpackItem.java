package com.megatrex4.items;

import com.google.common.collect.Multimap;
import com.megatrex4.items.screens.BackpackScreen;
import com.megatrex4.items.screens.BackpackScreenHandler;
import com.megatrex4.items.screens.ScreenHandlersRegistry;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.UUID;

public class BackpackItem extends TrinketItem implements BackpackItemData {

    private final int rows;

    public BackpackItem(Settings settings, int rows) {
        super(settings);
        this.rows = rows;
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = super.getModifiers(stack, slot, entity, uuid);
        if (slot != null && slot.inventory() != null && slot.inventory().size() > 0) {
            SlotAttributes.addSlotModifier(modifiers, "chest/backpack", uuid, 1, EntityAttributeModifier.Operation.ADDITION);
        }
        return modifiers;
    }

    @Override
    public void writeNbt(NbtCompound nbt, ItemStack stack) {
        // You might not need to call super here, or you may need a different method
        NbtCompound inventoryNbt = new NbtCompound();
        nbt.put("Container", inventoryNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt, ItemStack stack) {
        NbtCompound inventoryNbt = nbt.getCompound("Container");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(stack); // Send the item stack to the client
                }

                @Override
                public BackpackScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new BackpackScreenHandler(ScreenHandlersRegistry.BACKPACK_SCREEN_HANDLER_TYPE, syncId, playerInventory, stack);
                }

                @Override
                public Text getDisplayName() {
                    return stack.getName(); // or some custom name
                }
            });
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }






}
