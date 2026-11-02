# Enterprise POS Typography System

**Status:** Proposed; documentation only  
**Platform:** Android, Kotlin, Jetpack Compose, Material Design 3  
**Scope:** Typography audit and recommendations. No application source or UI has been changed.

## 1. Current typography audit

### Theme configuration

`MainActivity.kt` wraps the app navigation graph in `CashierSPTheme`. The theme passes the `Typography` value from `presentation/ui/theme/Type.kt` to `MaterialTheme`.

`Type.kt` only overrides `bodyLarge`, setting `FontFamily.Default`, Normal, `16.sp`, `24.sp` line height, and `0.5.sp` letter spacing. These values match the standard Material 3 `bodyLarge` defaults, so this is not a meaningful visual customization. Other roles use the Material 3 defaults. A second example for `titleLarge` and `labelSmall` is commented out and has no runtime effect.

`StockManagementScreen.kt` contains a nested `MaterialTheme` around a dropdown that changes `shapes` only; it inherits the surrounding typography.

### Fonts and dependency compatibility

- There are no font resources under `app/src/main/res/font`.
- The app uses `FontFamily.Default`, which resolves to the Android platform sans-serif (Roboto on standard Android builds) and retains Android's language fallback behavior.
- `SearchInvoiceModal.kt` explicitly uses `FontFamily.Monospace` for the invoice ID entry and placeholder, with `2.sp` letter spacing. No other active custom font family use was found.
- **`androidx.compose.material3:material3`** has no explicit version in `gradle/libs.versions.toml`; the app declares it via `libs.androidx.material3` and the Compose BOM `2024.09.00`. That BOM requests Material 3 `1.3.0`, but Gradle resolves the app's `debugRuntimeClasspath` to **`1.4.0`** because the Material 3 artifacts are in an atomic group and the separately versioned window-size-class artifact requires `1.4.0`.
- **`androidx.compose.material3:material3-window-size-class`** is separately declared as `1.4.0` in the version catalog and is also declared without a version in the app module. It resolves to **`1.4.0`**. Thus, its declared and resolved version is not the same as the BOM's requested Material 3 version; the Material 3 runtime artifact is aligned to it by Gradle conflict/group resolution.
- The root build file does not add another Compose BOM or Material 3 version override. These versions were verified with `:app:dependencyInsight --configuration debugRuntimeClasspath` for each artifact. The proposed roles below are the existing Material 3 `Typography` roles and `TextStyle` properties; they require no new dependency or API upgrade. `TextStyle.fontFeatureSettings` is also available for optional tabular numerals.

### Actual typography use

The source audit covered Kotlin under `app/src/main/java` (217 files). A search found 287 explicit numeric `fontSize` occurrences, 172 explicit font-weight occurrences, 24 explicit line-height occurrences, and only 14 references to `MaterialTheme.typography` roles. These are source occurrences, not a count of unique rendered text elements; some are conditional values and some can be helper/style definitions.

Most visible text therefore sets `fontSize` directly, often with a `fontWeight`, instead of using shared text roles. The explicit sizes span `9.sp` through `36.sp`; `9–11.sp` occurs in compact metadata and badges. Weights include equivalent spellings such as `W600` and `SemiBold`, and `W400` and `Normal`.

| Area | Observed implementation | Evidence and audit note |
|---|---|---|
| Cashier product list | Product name `13.sp` SemiBold with `15.sp` line height; item ID `10.sp`; price `15.sp` Bold; quantity `14.sp`; Add to cart `12.sp`. | `cashier/component/ItemCard.kt`. Small product name and button text, and a fixed 32dp title box, may become cramped at larger font scales. |
| Checkout/payment summary | Subtotal and payment detail text generally `14.sp`; total label `16.sp`, total amount `20.sp`; keypad labels use `12.sp` or `16.sp`. | `cashier/component/PaymentSummary.kt`. Reasonable emphasis exists, but roles are locally repeated rather than shared; other transaction details use different size choices. |
| Product details/inventory | Detail title `18.sp`; key stock and price values `16.sp`; metadata often `12–13.sp`. Inventory summary labels include `10.sp`, and a separate stock label uses `9.sp`. | `stock_management/component/ItemDetailBottomSheet.kt` and `stock_management/StockManagementScreen.kt`. The `9–10.sp` text is particularly difficult to scan and should not be used for important stock or status information. |
| Transaction history | Some items use Material roles, but commonly override them with explicit `fontSize` values; status labels are `10.sp`, dates `11.sp`, and amounts vary with length/expanded state. | `transaction_history/components/SalesReportSection.kt` and other transaction-history components. Applying a role and then overriding its size makes the role ineffective and obscures intended hierarchy. |
| Dashboard-style metrics | There is no separate dashboard route in `MainActivity.kt`; sales-report metrics are included in transaction history. The `SalesReportSection` mixes `10–12.sp` labels with `16.sp` metric values. | Do not treat a dashboard as an existing screen or introduce one as part of this proposal. |
| Forms | Login/register fields and field labels commonly specify `14.sp`; explanatory text varies from `12–15.sp`. Compact login headings switch between `28.sp` and `30.sp`, while a wider layout uses `36.sp`. | `login_register/LoginRegisterScreen.kt` and `login_register/component/LoginSide.kt` / `RegisterSide.kt`. Existing responsive sizing is manual and not consistently tied to semantic roles. |
| Dialogs and bottom sheets | Dialog titles often use `20.sp`; detail text is often `14.sp`. Inventory sheet values use `16.sp` and metadata `12–13.sp`. | Examples include `cashier/component/TransactionCompleteDialog.kt`, `cashier/component/GeneralAlertDialog.kt`, and `stock_management/component/ItemDetailBottomSheet.kt`. The hierarchy is understandable but independently specified across components. |
| Navigation drawer | Navigation item labels use `16.sp`, but merge a `14.sp` line height; section labels use `14.sp`. | `global_component/CashierDrawer.kt`. A line height smaller than the font size can crowd or clip glyphs, especially with larger system font settings. |
| Shared text helper | `TextWithNoPadding` merges `LocalTextStyle` with a custom line-height trimming/alignment policy and accepts optional explicit size and weight. | `global_component/TextWithNoPadding.kt`. This can produce different vertical metrics from regular Material `Text`; preserve only where the visual need is validated. |
| Navigation bars/tabs | No separate Material bottom-navigation-bar implementation was identified in the app route inventory. The app navigation observed in the shared shell is the drawer; segmented controls and filters appear within screens. | Avoid specifying a new navigation component as part of typography migration. Use the roles below for existing controls. |

### Main inconsistencies and usability risks

1. **Scattered sizing:** the shared theme offers a scale, but most screen text hardcodes a size. Common roles such as title, body, and label are not consistently used across cashier, stock, and transaction history.
2. **Very small operational text:** `9–11.sp` appears in stock summaries, IDs, dates, and status badges. These details can be difficult to read at cashier viewing distance and can become illegible at compact layouts or under accessibility scaling.
3. **Mixed hierarchy for similar content:** product price uses `15.sp` in the item list, while the key price in product details is `16.sp`; transaction totals use `20.sp`. The intent is sensible, but no shared role explains the difference.
4. **Role plus override:** some code sets `MaterialTheme.typography.bodySmall` or `titleMedium`, then supplies a different `fontSize`. This partially defeats the role and makes consistency harder to maintain.
5. **Line-height and font-padding overrides:** only a small minority of elements set explicit line height, but values vary and at least one navigation label uses a line height smaller than its type size. `TextWithNoPadding` also changes platform font padding/line trimming, so neighboring text may not align consistently.
6. **Inconsistent weight vocabulary:** equivalent weights are expressed through both named values and numeric aliases (`SemiBold`/`W600`, `Normal`/`W400`).
7. **Numerical alignment:** prices, quantities, invoice numbers, and totals are important scan targets. No `FontFeatureSettings("tnum")` use was found. The invoice ID field instead uses monospace and extra tracking, which is suitable only if that distinct treatment is intentional.
8. **Text scaling and fixed constraints:** fixed heights and truncation in compact product cards can clip or hide information when users increase Android font size. Important content should be allowed to wrap/scroll or use flexible height before being ellipsized.

## 2. Recommended font family

**Retain `FontFamily.Default` (Android system sans-serif / Roboto on standard Android).**

Roboto is already the effective typeface, aligns with Material Design and Android conventions, reads clearly at compact POS sizes, and has strong Latin/Indonesian support. Staying with the platform default avoids a font download, resource maintenance, licensing review, and unnecessary APK growth. System fallback remains available for text outside the primary Latin glyph set.

UI UX Pro Max's ecommerce/retail search returns Rubik and Nunito Sans as possible web typography pairings. Those are not installed in this Android project; adding them would be a separate product decision and would not be necessary to resolve the current consistency issues. Do not mix a second display family into the operational UI. Keep `FontFamily.Monospace` limited to identifiers only when a fixed-width appearance materially helps scanning.

## 3. Proposed Material 3 typography scale

Use the standard Material 3 role names and default metrics as the baseline. This gives a complete, familiar hierarchy without inventing a bespoke scale. The table spells out the intended values so the proposal can be reviewed and implemented consistently. All roles use `FontFamily.Default` unless a specific identifier treatment is called for.

| Role | Size / weight | Line height | Letter spacing | POS use |
|---|---|---:|---:|---|
| Display Large | `57.sp` / Normal | `64.sp` | `-0.25.sp` | Generally unused. Avoid this scale for cashier totals; it consumes valuable space and harms table density. |
| Display Medium | `45.sp` / Normal | `52.sp` | `0.sp` | Generally unused; reserve for an exceptional full-screen value presentation if one already exists. |
| Display Small | `36.sp` / Normal | `44.sp` | `0.sp` | Uncommon. Could serve a prominent value only in a roomy tablet layout after visual testing. |
| Headline Large | `32.sp` / Normal | `40.sp` | `0.sp` | Screen-level heading on roomy layouts; not for repeated list rows. |
| Headline Medium | `28.sp` / Normal | `36.sp` | `0.sp` | Screen heading on compact layouts where a larger hierarchy is useful. |
| Headline Small | `24.sp` / Normal | `32.sp` | `0.sp` | Section or dialog heading with strong emphasis. |
| Title Large | `22.sp` / Normal | `28.sp` | `0.sp` | Main screen title or prominent dialog title. |
| Title Medium | `16.sp` / Medium | `24.sp` | `0.15.sp` | Product names, card headings, key row values, and section headings. |
| Title Small | `14.sp` / Medium | `20.sp` | `0.1.sp` | Compact card headings, field labels when not using component defaults, and emphasized metadata. |
| Body Large | `16.sp` / Normal | `24.sp` | `0.5.sp` | Primary descriptions, supporting instructions, and readable long-form content. |
| Body Medium | `14.sp` / Normal | `20.sp` | `0.25.sp` | Default operational details, form input, transaction rows, and standard descriptions. |
| Body Small | `12.sp` / Normal | `16.sp` | `0.4.sp` | Secondary metadata only when non-critical and still legible at the user's font scale. |
| Label Large | `14.sp` / Medium | `20.sp` | `0.1.sp` | Buttons, tabs/segmented controls, and important short labels. |
| Label Medium | `12.sp` / Medium | `16.sp` | `0.5.sp` | Compact chips and secondary control labels; avoid for critical transaction states. |
| Label Small | `11.sp` / Medium | `16.sp` | `0.5.sp` | Rare supporting labels only. Prefer `12.sp` or larger for POS information that must be read quickly. |

The roles and properties above are available through the existing `androidx.compose.material3.Typography` / Compose `TextStyle` APIs in the repository's dependency set. This proposal does not require adopting a new typography API or upgrading Compose.

### Role adoption

- Prioritize `titleLarge`/`titleMedium`, `bodyLarge`/`bodyMedium`, and `labelLarge`/`labelMedium`; these cover most operational content.
- Use `bodySmall` or `labelSmall` sparingly for genuinely secondary metadata, not prices, stock quantities, errors, or transaction states.
- Keep display roles unused unless a current screen has a demonstrated need. Large text is not inherently clearer in a dense POS workflow.
- Do not force every existing text element into a different visual size during token migration. Map by meaning and preserve established hierarchy where it is already usable.

## 4. POS-specific guidelines

| Content | Recommended role | Guidance |
|---|---|---|
| Product name in a list/card | `titleSmall` or `titleMedium` | Prefer `titleMedium` where product selection is the primary task. Allow two or more lines where needed; do not constrain the text to a fixed height that clips at increased font scale. |
| Product price in a list | `titleMedium` | Keep weight and placement consistent across products. Use a numeric style suitable for aligned scanning. |
| Product detail price and stock quantity | `titleMedium` | Make the value visually distinct from its explanatory label; do not use color as the only emphasis. |
| Checkout total amount | `titleLarge` or `headlineSmall` | Pick one based on available width and preserve the same role across payment methods and confirmation views. Keep currency symbol and localized grouping. |
| Subtotal, discount, tax, change | `bodyMedium` | Align monetary values consistently (typically trailing/right aligned in tabular layouts). Avoid shrinking different values independently based on string length where wrapping or width can be handled deliberately. |
| Dashboard-style metric | `titleLarge` for value, `bodyMedium` for descriptor | Only applies to existing summary/metric content such as the sales report; no dashboard screen is present in the current route graph. |
| Transaction status | `labelLarge` or `labelMedium` | Keep “Paid”, “Pending”, “Failed”, etc. readable with a visible label and icon/other non-color cue. Do not reduce state text to tiny decorative metadata. |
| Form label and input | `bodyMedium` / component defaults | Preserve visible labels; placeholder text should not be the sole label. Use supporting text for format guidance and errors. |
| Validation or payment error | `bodyMedium` | Keep the explanation readable and close to its field/action; pair with semantic error indication and text. |
| Supporting descriptions | `bodyMedium` | Use `bodySmall` only when genuinely secondary; do not make explanatory text low contrast or excessively small. |

### Prices and quantities

- Use the same type role, weight, and alignment for values that users compare in a column or list.
- Apply tabular numeral features to aligned prices, quantities, totals, and transaction IDs when the active font supports them. In Compose, a style can be copied with `fontFeatureSettings = "tnum"`:

  ```kotlin
  val amountStyle = MaterialTheme.typography.titleMedium.copy(
      fontFeatureSettings = "tnum",
  )

  Text(
      text = formattedAmount,
      style = amountStyle,
      textAlign = TextAlign.End,
  )
  ```

- `tnum` gives digits equal advance widths; it does not replace locale-aware formatting. Keep the existing Indonesian Rupiah formatting and do not manually insert separators or change monetary behavior as part of typography work.
- Check decimal separators, currency prefixes, minus signs, and long amounts at narrow widths. Prefer wrapping or a deliberate single-line truncation policy to shrinking an important amount below readable size.
- Use monospace only for identifiers where fixed-width glyphs improve recognition. Invoice IDs can remain a candidate, but compare the current extra `2.sp` tracking with ordinary Roboto plus `tnum`; excessive tracking can make IDs harder to read or fit.

## 5. Phone and tablet considerations

- Keep semantic roles consistent across devices. A phone layout should usually gain space by reorganizing content, not by shrinking all text.
- Use larger titles and metric values on spacious tablet layouts only where visual hierarchy benefits; maintain body text sizes for legibility and information density.
- Respect Android font scaling: dimensions in `sp` scale with the user's font preference. Avoid fixed-height text containers, forced single lines, and tight vertical clipping around critical labels and values.
- Test at narrow phone widths and larger font scales, including long Indonesian labels and maximum realistic prices/quantities. Verify dialog and bottom-sheet content can scroll rather than truncate.
- Existing login/register layouts manually vary heading and subtitle sizes by layout class. When migrating, map those differences to semantic roles or a documented layout-specific role choice rather than retaining unrelated inline values. Do not change responsive layout behavior as part of this proposal.

## 6. Compose usage examples

### Use a Material role

```kotlin
Text(
    text = productName,
    style = MaterialTheme.typography.titleMedium,
    maxLines = 2,
)
```

### Derive a style for an intentional numerical variation

```kotlin
Text(
    text = total.toRupiah(),
    style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
    ),
)
```

Keep the base role's font family, line height, and tracking unless the component has a documented reason to override them. Prefer a named typography role to repeated inline `fontSize` values.

### Preserve field readability

```kotlin
OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    label = { Text("Search products") },
    textStyle = MaterialTheme.typography.bodyLarge,
)
```

Material 3 fields inherit theme typography and use their built-in label, placeholder, and supporting-text APIs. Avoid adding unnecessary raw size overrides to the field and its children.

## 7. Migration recommendations

1. Keep the approved `COLOR_SYSTEM.md` unchanged; typography is a separate system.
2. Replace the placeholder `bodyLarge`-only definition with the reviewed Material 3 role mapping, retaining `FontFamily.Default` and the existing dependency set.
3. Migrate by semantic content, starting with checkout totals, product prices/quantities, transaction status, and field/error text. Those values have the greatest operational consequence.
4. Replace direct `fontSize` and repeated `fontWeight` declarations with a role where the intent matches. Avoid mechanically mapping every size to the nearest role; explicitly retain justified exceptions.
5. Remove size overrides that are layered over `MaterialTheme.typography` unless the difference is intentional and documented.
6. Audit `TextWithNoPadding` and any `PlatformTextStyle` / line-height trimming usage. Keep them only where visual tests confirm they are needed and do not impair accessibility font scaling.
7. Standardize font-weight naming and line-height policy; do not use line heights smaller than their corresponding type size.
8. Add tabular numeral settings to aligned numerical groups after validating the typeface and output; preserve localized number formatting.
9. Validate forms, product selection, inventory, transaction history, dialogs, bottom sheets, and navigation at phone/tablet widths and increased system font scales. Check line wraps, clipping, focus, and TalkBack reading order.
10. Keep font resources and dependencies unchanged unless a later, separately approved requirement identifies a material benefit from a custom family.

## References

- [Material Design 3 typography](https://m3.material.io/styles/typography/overview)
- [Material 3 in Jetpack Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Compose text styling](https://developer.android.com/develop/ui/compose/text/style-text)
- [Android accessibility: scalable text](https://developer.android.com/guide/topics/ui/accessibility/principles)
- [WCAG 2.2, SC 1.4.4 Resize Text](https://www.w3.org/TR/WCAG22/#resize-text)
- [UI UX Pro Max prompt and Jetpack Compose stack guidance](../../.github/prompts/ui-ux-pro-max/PROMPT.md)
