# 验证记录

日期：2026-10-21（America/Los_Angeles）。模组版本：2.0.0-beta.1。

## 环境与来源

- Minecraft 1.21.1；NeoForge 21.1.264；ModDevGradle 2.0.148；Gradle 9.2.1。
- Java 21.0.9；macOS Apple Silicon。
- 原仓库参考提交：`a9c38c3ed0e6aba245267a02b4c99f45abf11f8f`。
- MDK 参考提交：`f7002b1b6011723c7b12fde95147689491a6ddab`。

## 最终构建与专用服务器测试

执行 `./gradlew build runGameTestServer`，结果为 `BUILD SUCCESSFUL`。

测试服务器报告 **15 项自定义 required tests 全部通过**。

自定义测试在实际 Minecraft 菜单、配方和物品组件环境下执行，通过调用玩家 tick 处理器推进任务；它们没有模拟完整鼠标输入或全部网络流量。

| 用例 | 验证内容 |
|---|---|
| duration_policy | 默认公式、四格原料、产物倍率、经验等级上限、零倍率与小数速度取整 |
| inventory_single | 完成前无产物，等待后四块木板、只消耗一根原木、不自动重复 |
| inventory_right_click | 右键也等待一次完整配方 |
| shift_each_recipe_waits | 三次配方逐次等待、每次只获得四块木板 |
| input_change_cancels | 中途原料数量改变即取消，无材料消耗 |
| closing_cancels | 菜单实例变更后旧任务无法完成 |
| full_inventory | 背包已满时拒绝连续制作，原料保留 |
| alternate_clicks_blocked | 数字键、丢弃、克隆、拖拽、双击收集无法直接取得结果 |
| cursor_capacity | 鼠标只能放下部分产物时不开始制作 |
| attribute_speed | 原生速度属性 2 倍时等待 10 tick |
| table_and_bucket_remainders | 真实 3×3 蛋糕配方等待 180 tick，原版返还三个空桶 |
| ingredient_output_multipliers | 原料物品/模组、产物及菜单倍率共同生效，不乘原料堆叠数量 |
| anvil_cost_and_rename | 铁砧结果无法提前取出，保留名称并在完成时扣除经验和原料 |
| smithing_upgrade | 钻石剑升级保留下界合金剑结果，完成时消耗模板、剑和锭 |
| stonecutter_timed | 切石配方等待后仅消耗一个输入，获得所选配方结果 |

构建与测试日志由运行命令生成，游戏日志位于 `run/logs`。

## 客户端与真实连接验证

使用临时测试世界 `CTTValidation`，通过原版 `handleInventoryMouseClick` 发送真实容器点击包。客户端与内置服务器间使用 NeoForge payload 注册及实际序列化路径。

测试使用全新的临时存档，等待生存模式同步完成，在开发测试模组中将全局倍率临时设为 3，经验等级设为 0，使一根原木的合成耗时为 60 tick，便于观察进度。该值仅在自动测试运行时设置；默认全局倍率为 1。

已验证：

1. 原版背包界面能正常显示四块木板的配方预览。
2. 客户端点击后没有预测出即时产物。
3. 客户端收到服务端进度，`total = 60`，结果槽下方显示绿色进度条。
4. 完成后客户端鼠标上有四块木板，原料从两根原木变为一根。
5. 客户端和内置服务器正常关闭。

日志标记：

```text
CTT_CLIENT_INTEGRATION_PASSED: native click packet, 60-tick progress received, one log consumed, four planks on cursor
```

截图由 Minecraft 自身截图接口保存，见 [游戏内截图](docs/crafting-progress.png)。重跑客户端验证时会在控制台输出上述标记。

可重跑的步骤：

```sh
./gradlew runGameTestServer
python3 tools/prepare_client_validation.py
./gradlew runClient -PclientIntegration
```

`run/saves/CTTValidation` 为专门的测试世界；准备脚本在它已存在时停止，避免覆盖。该选项会自动打开此测试世界、准备原料、测试制作并退出客户端；常规 `runClient` 不执行自动操作。

## 尚未验证的范围

- 远程专用服务器加多位真实玩家，以及高延迟、断线重连下的体验。
- JEI、InvMove、Curios、Crafting Station 等第三方模组组合。
- 自定义工作台独立材料仓库、能量系统或自定义网络制作路径。
- 超大物品堆叠模组、所有特殊/数据包配方及其他模组的合成事件副作用。
- 全部界面尺寸、缩放与资源包下的视觉效果；目前游戏内截图验证了原版背包界面。
- 旧配置自动迁移及旧版所有自定义覆盖层选项。

当前版本适合试玩与继续迭代。兼容性声明以实际测试过的菜单和路径为限。
