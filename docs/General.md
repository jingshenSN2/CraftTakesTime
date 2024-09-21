# General Information

This mod forces players to spend time crafting, when you try to craft things in the inventory/craft table, you will see a progress overlay indicating the remaining time.

## How do you customize the craft time?

The config file of this mod is located in `config/crafttakestime/craft_time_config.json`. There are several levels of craft speed config:

a. Global Multiplier (`globalCraftingTimeMultiplier`): You can set a global multiplier for the craft time. The larger the value, the longer crafting takes.

b. Container Multiplier (`containers.[container].craftingTimeMultiplier`): You can specify which container is easier/harder to craft. The larger the value, the longer crafting takes.

c. Ingredient Mod Multiplier (`ingredientConfig.modCraftingTimeMultipliers.[modid]`): You can specify a mod that makes the craft easier/harder. This applies to all items in that mod when used as ingredients.

d. Ingredient Item Multiplier (`ingredientConfig.itemCraftingTimeMultipliers.[item_name]`): You can specify an item that makes the craft easier/harder when they are used as ingredients.

e. Output Mod Multiplier (`outputConfig.modCraftingTimeMultipliers.[modid]`): You can specify which mod's item makes the craft easier/harder. This applies to all items in that mod when they are the output item.

f. Output Item Multiplier (`outputConfig.itemCraftingTimeMultipliers.[item_name]`): You can specify an item that makes the craft easier/harder when they are the output item.

Overall, the craft time (unit: ticks) will be:

20 x `global_multiplier` x `output_item_multiplier` x `output_mod_multiplier` x sum_by_ingredients(`ingredient_item_multiplier` x `ingredient_mod_multiplier`)

You also have a 2% boost in speed for each player's experience level (up to 200 levels).

For example:

a. All default settings, the player is level 0, crafting wood planks from a log:

Time = 20 x 1 x 1 x 1 x (1 x 1) = 20 ticks

b. All default settings, the player is level 0, crafting a crafting table from 4 planks:

Time = 20 x 1 x 1 x 1 x (1 x 1 + 1 x 1 + 1 x 1 + 1 x 1) = 80 ticks

c. All default settings, the player is level 0, crafting a crafting table from 4 planks, but outputConfig.modCraftingTimeMultipliers.minecraft is set to 2.0:

Time = 20 x 1 x 2.0 x 1 x (1 x 1 + 1 x 1 + 1 x 1 + 1 x 1) = 160 ticks

d. All default settings, the player is level 0, crafting a crafting table from 4 planks, but ingredientConfig.modCraftingTimeMultipliers.minecraft is set to 0.5 and ingredientConfig.itemCraftingTimeMultipliers.minecraft:oak_planks is set to 0.5:

Time = 20 x 1 x 1 x 1 x (0.5 x 0.5 + 0.5 x 0.5 + 0.5 x 0.5 + 0.5 x 0.5) = 20 ticks

## How do you customize a new crafting table/tool station to take time to craft?

TBD. This is an advanced feature. Leave a comment if you want to know how to do it and I'll update it here.
