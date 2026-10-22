# Craft Takes Time 数据包与物品组件

适用于 NeoForge 版 `2.0.0-beta.2`，Minecraft 1.21.1 和 26.3。两版使用相同的数据文件结构与字段，`pack.mcmeta` 使用各自的游戏数据包格式。

## 安装与重载

1. 选择对应版本的 `examples/crafttakestime-settings` 文件夹，或下载发布包内对应的示例数据包 ZIP。
2. 将文件夹或 ZIP 放入世界的 `datapacks` 文件夹。ZIP 根目录直接包含 `pack.mcmeta` 和 `data`。
3. 执行 `/reload`，使用 `/datapack list` 检查加载状态；已被禁用的包可用 `/datapack enable "file/包名"` 启用。
4. 按需要编辑数据文件，再执行 `/reload`。

单人存档路径通常为 `.minecraft/saves/<世界名>/datapacks/`，服务器为 `<世界目录>/datapacks/`。数据包由服务器加载；客户端和服务器均安装 Craft Takes Time 模组。联机玩家通过模组的原生网络注册接收同步数据及制作进度。

默认无需额外数据包即可游玩：所有倍率为 1，音效开启。示例包演示原木作为原料耗时减半、钻石镐作为产物耗时三倍、工作台倍率 0.5、铁砧倍率 2；安装后这些示例会生效。

Minecraft 1.21.1 的 `pack.mcmeta`：

```json
{"pack":{"pack_format":48,"description":"Craft Takes Time settings"}}
```

Minecraft 26.3 的 `pack.mcmeta`：

```json
{"pack":{"min_format":[121,0],"max_format":[121,0],"description":"Craft Takes Time settings"}}
```

## 物品与标签耗时

文件：`data/crafttakestime/data_maps/item/crafting_time.json`

```json
{
  "values": {
    "#minecraft:logs": {"ingredient_multiplier": 0.5},
    "minecraft:diamond_pickaxe": {"output_multiplier": 3.0},
    "minecraft:stick": {"ingredient_multiplier": 0.25, "output_multiplier": 1.5}
  }
}
```

`ingredient_multiplier` 控制物品作为原料时的倍率，`output_multiplier` 控制它作为产物时的倍率。省略的字段为 1。键可以是原版或其他模组的物品注册 ID，也可以是 `#` 开头的物品标签。自定义标签使用标准 `data/<命名空间>/tags/item/<名称>.json` 文件。

每个非空原料槽计一次倍率，槽内物品堆叠数量不影响单次配方耗时。一次原木配方得到四块木板仍算一次制作。

## 工作台与其他菜单耗时

文件：`data/crafttakestime/data_maps/menu/crafting_time.json`

```json
{
  "values": {
    "minecraft:crafting": 0.5,
    "minecraft:anvil": 2.0,
    "minecraft:smithing": 1.0,
    "minecraft:stonecutter": 1.0
  }
}
```

键使用 `MenuType` 注册 ID。背包 2×2 合成没有注册的 `MenuType`，使用下面的 `inventory_multiplier`。其他模组的菜单具有制作适配器时，可以用它的真实菜单注册 ID 设置倍率；增加倍率条目本身不改变该菜单的制作实现。

## 全局、背包、命名空间与音效

文件：`data/crafttakestime/data_maps/entity_type/crafting_settings.json`

```json
{
  "values": {
    "minecraft:player": {
      "global_multiplier": 1.0,
      "inventory_multiplier": 1.0,
      "crafting_sound": true,
      "ingredient_mod_multipliers": {"minecraft": 1.0},
      "output_mod_multipliers": {"minecraft": 1.0}
    }
  }
}
```

公共规则附着在玩家实体类型 `minecraft:player` 的 Data Map 上。`global_multiplier` 作用于所有支持的制作，`inventory_multiplier` 作用于背包合成，`crafting_sound` 控制制作与完成音效。命名空间倍率的键是物品 ID 的冒号前部分，例如 `minecraft` 或其他模组的命名空间；它与物品倍率相乘。

省略时，全局与背包倍率为 1，音效为 `true`，两个命名空间表为空，未列出的命名空间倍率为 1。所有倍率均为 0 至 1,000,000 的有限数值。格式或数值错误由 NeoForge 加载器报告，修改后可再次重载。

## 单个物品堆的组件

持久化物品组件 `crafttakestime:crafting_time` 使用相同的两个字段。命令示例：

```mcfunction
/give @s minecraft:oak_log[crafttakestime:crafting_time={ingredient_multiplier:0.25,output_multiplier:1.0}] 1
```

该原木作为原料时使用 0.25 倍率。组件可以通过支持组件的配方结果、战利品函数或其他模组设置，并通过 Minecraft 的物品保存及同步系统保留。

组件覆盖该物品堆从物品 Data Map 得到的整条记录，省略的组件字段取 1。命名空间、菜单、全局与玩家速度规则继续生效。

Data Maps 提供物品类型或标签的默认规则；组件提供单个物品堆的规则。组件类型由 Craft Takes Time 注册，数据加载、持久化和同步使用 Minecraft/NeoForge 的标准系统。

## 玩家与装备速度

玩家速度使用原生 attribute `crafttakestime:crafting_speed`。默认值为 1，值为 2 表示合成速度翻倍：

```mcfunction
/attribute @s crafttakestime:crafting_speed base set 2
```

装备可以通过原生 `minecraft:attribute_modifiers` 组件在指定装备槽提供该属性的加成，其他模组也可以添加属性修饰器。装备属性描述佩戴者的速度；原料和产物的制作倍率使用前述 Data Map 或物品堆组件。

```text
ticks = ceil(
  20 × global × menu × output × output_namespace
  × Σ非空原料槽(ingredient × ingredient_namespace)
  ÷ (1 + 0.02 × clamp(经验等级, 0, 200)) ÷ crafting_speed
)
```

最短为 1 tick。服务端按每秒 20 tick 推进，实际时间受服务器运行速度影响。每次开始制作时固定本次耗时；Shift 连续制作在每次配方开始时重新计算。

## 优先级与迁移

多个数据包使用 NeoForge Data Maps 的标准加载顺序与覆盖规则。较高优先级的同一条目替换整条值，省略字段使用默认值；本模组没有额外的逐字段合并器。需要组合规则时，在最终生效的条目中写全需要保留的字段。物品堆组件的优先级高于物品 Data Map。

`/reload` 成功应用数据时，已有任务会在下一个玩家 tick 取消，材料保持原样。重新点击后按当前数据开始制作。

旧文件 `config/crafttakestime/craft_time_config.json` 保留在磁盘。当前版本的配置来源为上述数据包，运行时不再生成或读取旧文件。自定义数值按以下对应关系手动迁移：

| 旧字段 | 数据包位置或新字段 |
|---|---|
| `globalCraftingTimeMultiplier` | 玩家规则 `global_multiplier` |
| `enableCraftingSound` | 玩家规则 `crafting_sound` |
| `containerCraftingTimeMultipliers.minecraft:inventory` | 玩家规则 `inventory_multiplier` |
| 其他 `containerCraftingTimeMultipliers` | 菜单 Data Map 的注册 ID 条目 |
| `ingredientConfig.itemCraftingTimeMultipliers` | 物品 Data Map 的 `ingredient_multiplier` |
| `outputConfig.itemCraftingTimeMultipliers` | 物品 Data Map 的 `output_multiplier` |
| `ingredientConfig.modCraftingTimeMultipliers` | 玩家规则 `ingredient_mod_multipliers` |
| `outputConfig.modCraftingTimeMultipliers` | 玩家规则 `output_mod_multipliers` |

参考：[NeoForge Data Maps](https://docs.neoforged.net/docs/1.21.1/resources/server/datamaps/)、[NeoForge Data Components](https://docs.neoforged.net/docs/1.21.1/items/datacomponents/)。
