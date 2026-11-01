# Enterprise POS Color System Proposal

**Status:** Proposed; not yet implemented  
**Platform:** Android, Jetpack Compose, Material Design 3  
**Scope:** Color tokens and theme migration only. No screen redesign or behavior changes are included.

## 1. Goals and existing palette review

The app already has a recognizable orange-and-navy identity:

- Brand orange: `#FE9F43` (`Primary`)
- Brand navy: `#092C4C` (`Secondary`)
- Main surfaces: white and near-white colors such as `#FFFFFF` and `#FAFAFA`
- Supporting text: a gray ramp from `#D3D5D7` through `#212B36`

The current theme does not yet express that palette as a Material 3 scheme. `Theme.kt` defines default purple/pink Material roles, comments out dynamic and dark scheme selection, and always applies the light scheme. Presentation code instead imports many values directly from `Color.kt`. The palette file contains numerous long shade ramps, while a few composables also use inline colors, for example `#D64545`, `#E5E7EB`, and `#FFF4E6`. This creates parallel sources of truth.

The existing orange is appropriate as a brand color and on pale orange surfaces, but it is not suitable as a background for white text. Its contrast with white is approximately **2.05:1**. The proposal keeps it available as a decorative/brand tint, but uses a deeper orange for text-bearing primary actions.

The repository's UI UX Pro Max prompt recommends a professional, high-contrast enterprise palette and semantic colors. Its native Compose stack guidance favors theme tokens and state-driven presentation. The proposal below uses those principles with Material 3 roles, keeping the POS brand rather than adopting the prompt's unrelated sample palette.

### Current color usage observed in presentation code

| Existing group | Observed use | Audit note |
|---|---|---|
| `Primary`, `PrimaryHover`, `Primary100`–`Primary900` | Main buttons, totals, progress indicators, chips, badges, and pale orange card/sheet fills | The same bright orange is used for both emphasis and text-bearing controls; assign explicit role pairs rather than preserving shade names by number. |
| `Secondary`, `SecondaryHover`, `Secondary100`–`Secondary900` | Main text, navigation controls, page backgrounds, and borders | Navy is used effectively for emphasis, but `Secondary100` is also used as a broad page background and conflicts with several `Light*` page surfaces. |
| `Gray*`, `Light*`, `White`, `Black`, `Dark` | Text, backgrounds, dividers, disabled states, and card surfaces | Large ramps are used directly in screen components; review each mapping instead of assuming nearby shade numbers have equivalent contrast. |
| `Success*`, `Warning*`, `Danger*`, `Info*` and other hue ramps | Payment/stock state badges, errors, and status icons | Semantic meanings exist, but the palette is not tied to light/dark pairs and status text may use small sizes. |
| Inline presentation colors | Examples include red `#D64545`, neutral `#E5E7EB`/`#1E293B`, orange-tinted `#FFF4E6`/`#FFF7ED`, and success/error colors in transaction dialogs | These bypass the theme and can diverge from approved semantic colors; consolidate them during migration. |

## 2. Proposed core palette

| Token | Light HEX | Dark HEX | Intended use |
|---|---:|---:|---|
| Brand orange (original) | `#FE9F43` | `#FE9F43` | Brand illustration, non-text accent, or decorative highlight. Do not put white text on it. |
| Primary | `#A64B00` | `#FFB77D` | Main CTA, selected navigation/control state, focus indication where appropriate. |
| On primary | `#FFFFFF` | `#552000` | Foreground on primary buttons and filled controls. |
| Primary container | `#FFDBC2` | `#783500` | Soft selected/brand-tinted surfaces, chips, and low-emphasis callouts. |
| On primary container | `#351000` | `#FFDBC2` | Text/icons on primary containers. |
| Secondary (brand navy) | `#092C4C` | `#A9C7E4` | Supporting brand emphasis, secondary controls, and navigation accents. |
| On secondary | `#FFFFFF` | `#102C45` | Foreground on secondary controls. |
| Secondary container | `#D4E3F3` | `#24435D` | Muted navy-tinted surfaces and selected secondary elements. |
| On secondary container | `#0A2238` | `#D4E3F3` | Text/icons on secondary containers. |
| Tertiary (teal) | `#006B5F` | `#85D6C7` | Supporting visual emphasis only; not a transaction status color and not a second primary CTA. |
| On tertiary | `#FFFFFF` | `#00382F` | Foreground on tertiary elements. |
| Tertiary container | `#A5F2E2` | `#005047` | Subtle supporting visual surfaces. Do not use for transaction status badges. |
| On tertiary container | `#00201A` | `#A5F2E2` | Text/icons on tertiary containers. |
| Background | `#F8F9FC` | `#111418` | App window/page background. |
| On background | `#191C20` | `#E1E2E6` | Primary text on background. |
| Surface | `#FFFFFF` | `#191C20` | Cards, sheets, dialogs, text fields, and navigation surfaces. |
| On surface | `#191C20` | `#E1E2E6` | Primary content on surfaces. |
| Surface variant | `#E7E9ED` | `#42474D` | Lower-emphasis grouped areas, disabled containers, and tonal separation. |
| On surface variant | `#41474D` | `#C1C7CE` | Secondary labels and content on surface variant. |
| Outline | `#6D747C` | `#8B929A` | Text-field borders and controls whose boundary must remain visible. |
| Outline variant | `#C1C7CE` | `#42474D` | Low-emphasis separators and decorative card borders. |
| Error | `#BA1A1A` | `#FFB4AB` | Error text, icons, and destructive emphasis. |
| On error | `#FFFFFF` | `#690005` | Foreground on error-filled controls. |
| Error container | `#FFDAD6` | `#93000A` | Error banners and error-state surfaces. |
| On error container | `#410002` | `#FFDAD6` | Text/icons on error containers. |

### Why the primary orange is deeper

`#A64B00` retains a recognizable warm orange while achieving **5.79:1** contrast for white text. It can therefore support filled buttons such as Continue, Add to cart, and Print receipt. The current `#FE9F43` remains in the system as the original brand orange, useful for non-text decorative accents or as a background paired with dark navy text. Do not use the original bright orange as a text-bearing button with white labels.

The navy stays close to the existing `#092C4C` and is already highly legible with white. Teal is introduced only as a supporting visual tertiary role (for example, a non-status illustration or secondary highlight). It must not encode paid/success or any other transaction status; those meanings use the separate semantic status mappings below.

## 3. Material 3 ColorScheme mapping

The app imports the Compose BOM `2024.09.00`; its `androidx.compose.material3:material3` artifact is managed at **1.3.0**. The app also declares `material3-window-size-class:1.4.0` separately, but that does not change the Material 3 component artifact's ColorScheme API version. The mappings below are valid for Material 3 1.3.0.

In 1.3.0, `background`, `onBackground`, `surfaceVariant`, and `onSurfaceVariant` are supported ColorScheme properties and are not deprecated. The `surfaceContainer*` roles belong to later Material 3 API releases; do not substitute them in this proposal unless the Material 3 dependency is deliberately upgraded and the project is rechecked. This avoids recommending newer roles that are not in the app's BOM-managed artifact. Newer library versions may deprecate/replace some roles, so reassess when upgrading rather than applying current online API annotations retroactively to 1.3.0.

### Light scheme

```kotlin
lightColorScheme(
    primary = Color(0xFFA64B00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC2),
    onPrimaryContainer = Color(0xFF351000),
    secondary = Color(0xFF092C4C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD4E3F3),
    onSecondaryContainer = Color(0xFF0A2238),
    tertiary = Color(0xFF006B5F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFA5F2E2),
    onTertiaryContainer = Color(0xFF00201A),
    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFE7E9ED),
    onSurfaceVariant = Color(0xFF41474D),
    outline = Color(0xFF6D747C),
    outlineVariant = Color(0xFFC1C7CE),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)
```

### Dark scheme

```kotlin
darkColorScheme(
    primary = Color(0xFFFFB77D),
    onPrimary = Color(0xFF552000),
    primaryContainer = Color(0xFF783500),
    onPrimaryContainer = Color(0xFFFFDBC2),
    secondary = Color(0xFFA9C7E4),
    onSecondary = Color(0xFF102C45),
    secondaryContainer = Color(0xFF24435D),
    onSecondaryContainer = Color(0xFFD4E3F3),
    tertiary = Color(0xFF85D6C7),
    onTertiary = Color(0xFF00382F),
    tertiaryContainer = Color(0xFF005047),
    onTertiaryContainer = Color(0xFFA5F2E2),
    background = Color(0xFF111418),
    onBackground = Color(0xFFE1E2E6),
    surface = Color(0xFF191C20),
    onSurface = Color(0xFFE1E2E6),
    surfaceVariant = Color(0xFF42474D),
    onSurfaceVariant = Color(0xFFC1C7CE),
    outline = Color(0xFF8B929A),
    outlineVariant = Color(0xFF42474D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)
```

These are proposed static brand schemes. For a POS app where status colors and brand consistency matter, keep wallpaper-based dynamic color disabled by default; otherwise users may see product and payment emphasis shift with their wallpaper. Honor the system light/dark setting with the corresponding branded scheme.

### Practical Material 3 component examples

These are role-usage examples only, not a screen redesign. Standard Material 3 components inherit the app scheme by default; customize only when the component intentionally represents a non-primary semantic role.

#### Primary and secondary buttons

```kotlin
Button(
    onClick = onCheckout,
    // FilledButton defaults use primary / onPrimary from MaterialTheme.colorScheme.
) {
    Text("Checkout")
}

Button(
    onClick = onAlternateAction,
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
    ),
) {
    Text("Secondary action")
}
```

Use the standard primary filled treatment for the principal action. Reserve a filled navy treatment for an intentional secondary action; use `OutlinedButton` or `TextButton` for lower-emphasis actions. Do not use the original bright orange with white button text.

#### Transaction status badge

```kotlin
@Composable
fun TransactionStatusBadge(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(label)
        }
    }
}
```

The visible label is required even when an icon and color are present. Choose the state’s semantic foreground and matching container pair from the table below. Supply a status-specific icon; do not use teal as a status color.

#### Product card

```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ),
) {
    Column(Modifier.padding(16.dp)) {
        Text(productName, style = MaterialTheme.typography.titleMedium)
        Text(
            productDescription,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            formattedPrice,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
```

Use primary only for the price emphasis or a clear action; keep product name and ordinary details on surface roles.

#### Text field

```kotlin
OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    label = { Text("Search products") },
    colors = OutlinedTextFieldDefaults.colors(),
)
```

Material 3 defaults consume the active scheme for text, outlines, cursor, and error state. Use an explicit label and supporting/error text; do not add a separate hardcoded color palette to the field.

#### Dialog

```kotlin
AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Confirm checkout") },
    text = { Text("Review the payment before completing this sale.") },
    confirmButton = {
        TextButton(onClick = onConfirm) { Text("Confirm") }
    },
    dismissButton = {
        TextButton(onClick = onDismiss) { Text("Cancel") }
    },
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
```

Use `error`/`onError` only for destructive or error emphasis, and retain clear action labels. Dialogs should remain readable in both schemes.

## 4. Semantic status colors

Material 3's core `ColorScheme` has an error role but no standard success, warning, or information roles. Keep these as a small, centrally defined semantic token set—not inline colors in individual composables. Always pair status color with a text label, icon, or other non-color cue.

### POS transaction status mapping

These assignments are intentionally distinct from the supporting tertiary teal. A status badge must include a visible status label and a state-specific icon; color is a redundant cue only. For example, distinguish “Paid” from “Refunded” in text and icon even though both represent completed operations.

| Transaction state | Meaning | Light foreground / container / on-container | Dark foreground / container / on-container |
|---|---|---|---|
| Paid / Success | Payment completed successfully | `#1B6E3B` / `#D7F2DE` / `#123B22` | `#8CD99D` / `#173622` / `#8CD99D` |
| Pending | Payment or transaction is awaiting completion | `#765000` / `#FFF2CC` / `#3B2E00` | `#FFD166` / `#493400` / `#FFD166` |
| Failed | Payment attempt failed | `#BA1A1A` / `#FFDAD6` / `#410002` | `#FFB4AB` / `#93000A` / `#FFDAD6` |
| Refunded | Funds have been returned; informational, not “Paid” | `#005AA7` / `#D6E8FF` / `#102F4A` | `#9CCBFF` / `#102F4A` / `#9CCBFF` |
| Cancelled | Transaction was cancelled; neutral outcome, not a failure | `#41474D` / `#E7E9ED` / `#41474D` | `#C1C7CE` / `#42474D` / `#C1C7CE` |

Column values are `foreground/accent`, `container`, and `on-container text/icon` respectively. Use the foreground/accent on neutral surfaces; use `on-container` on the listed status container. For payment-status icons, use recognizable state-specific symbols (for example, check, clock, error, return, and cancel) with a visible text label. Do not rely on color, shape, or icon alone.

General semantic tokens remain available outside transaction statuses:

| Semantic meaning | Light foreground / container / on-container | Dark foreground / container / on-container | Intended use |
|---|---|---|---|
| Success | `#1B6E3B` / `#D7F2DE` / `#123B22` | `#8CD99D` / `#173622` / `#8CD99D` | Non-transaction success feedback where appropriate. |
| Warning | `#765000` / `#FFF2CC` / `#3B2E00` | `#FFD166` / `#493400` / `#FFD166` | Caution or attention needed; pending transaction is an explicit status mapping above. |
| Information | `#005AA7` / `#D6E8FF` / `#102F4A` | `#9CCBFF` / `#102F4A` / `#9CCBFF` | Informational notices; refunded transaction is an explicit status mapping above. |
| Error | `#BA1A1A` / `#FFDAD6` / `#410002` | `#FFB4AB` / `#93000A` / `#FFDAD6` | Error feedback; failed transaction is an explicit status mapping above. |

Do not use success green to mean “selected” or orange to mean “warning.” The teal tertiary role is never the transaction success color.

## 5. Contrast verification

Ratios below are calculated from the proposed sRGB HEX values using the WCAG relative-luminance formula. Text combinations are designed to exceed **4.5:1** for normal text and **3:1** for large text. WCAG defines large text as at least 18pt regular or 14pt bold; do not treat typical 14–18sp Android labels as large text automatically.

| Foreground | Background | Ratio | Application |
|---|---|---:|---|
| `#FFFFFF` | Primary `#A64B00` | **5.79:1** | Light primary button label |
| `#FFFFFF` | Navy `#092C4C` | **14.23:1** | Light secondary button label |
| `#351000` | Primary container `#FFDBC2` | **13.19:1** | Light selected chip/container text |
| `#0A2238` | Secondary container `#D4E3F3` | **12.38:1** | Light secondary container text |
| `#FFFFFF` | Tertiary `#006B5F` | **6.43:1** | Light tertiary control label |
| `#00201A` | Tertiary container `#A5F2E2` | **13.42:1** | Light tertiary container text |
| `#191C20` | Background `#F8F9FC` | **16.24:1** | Light primary content |
| `#191C20` | Surface `#FFFFFF` | **17.10:1** | Light card/dialog content |
| `#41474D` | Surface variant `#E7E9ED` | **7.74:1** | Light secondary content on variant |
| `#FFFFFF` | Error `#BA1A1A` | **6.46:1** | Light error control label |
| `#410002` | Error container `#FFDAD6` | **13.26:1** | Light error message text |
| `#1B6E3B` | Success container `#D7F2DE` | **5.28:1** | Light success badge accent |
| `#123B22` | Success container `#D7F2DE` | **10.54:1** | Light success badge label |
| `#765000` | Warning container `#FFF2CC` | **6.45:1** | Light pending/warning badge accent |
| `#3B2E00` | Warning container `#FFF2CC` | **11.94:1** | Light pending/warning badge label |
| `#BA1A1A` | Error container `#FFDAD6` | **5.00:1** | Light failed/error badge accent |
| `#410002` | Error container `#FFDAD6` | **13.26:1** | Light failed/error badge label |
| `#005AA7` | Information container `#D6E8FF` | **5.58:1** | Light refunded/information badge accent |
| `#102F4A` | Information container `#D6E8FF` | **11.03:1** | Light refunded/information badge label |
| `#41474D` | Neutral container `#E7E9ED` | **7.74:1** | Light cancelled badge label |
| `#552000` | Dark primary `#FFB77D` | **7.74:1** | Dark primary button label |
| `#102C45` | Dark secondary `#A9C7E4` | **8.16:1** | Dark secondary button label |
| `#00382F` | Dark tertiary `#85D6C7` | **7.74:1** | Dark tertiary control label |
| `#E1E2E6` | Dark background `#111418` | **14.27:1** | Dark primary content |
| `#C1C7CE` | Dark surface variant `#42474D` | **5.50:1** | Dark secondary content on variant |
| `#FFB4AB` | Dark error container `#93000A` | **5.51:1** | Dark failed/error badge accent |
| `#FFDAD6` | Dark error container `#93000A` | **7.24:1** | Dark failed/error badge label |
| `#8CD99D` | Dark success container `#173622` | **7.89:1** | Dark success message text |
| `#FFD166` | Dark warning container `#493400` | **8.21:1** | Dark warning message text |
| `#9CCBFF` | Dark information container `#102F4A` | **8.12:1** | Dark information message text |
| `#C1C7CE` | Dark neutral container `#42474D` | **5.50:1** | Dark cancelled badge label |
| `#FFFFFF` | Original orange `#FE9F43` | **2.05:1 — fail** | Do not use for normal or large white button text |

Outline roles are for boundaries, not ordinary text. The proposed light `outline` is `4.73:1` against white and `4.49:1` against the page background; dark `outline` is `5.44:1` against the dark surface. These boundary pairs exceed the 3:1 non-text contrast target. `outlineVariant` is intentionally quieter for decorative dividers and must not be the only indication of an important interactive boundary.

Ratios depend on the exact rendered foreground/background pair. Opacity, disabled-state alpha, image content, tonal overlays, and custom component colors can reduce contrast; verify those states separately. Never convey transaction status, selection, or errors by color alone.

## 6. Usage guidelines

| UI element | Recommended roles |
|---|---|
| Main actions (checkout/continue/confirm) | `primary` + `onPrimary`; prefer standard Material 3 filled buttons and accessible minimum touch targets. |
| Secondary actions | `secondary` + `onSecondary`, or outlined/text button roles on a surface. Avoid making every action orange. |
| Selected category/filter | `secondaryContainer`/`onSecondaryContainer` or `primaryContainer`/`onPrimaryContainer`; use a clear selected shape/state as well. |
| Page background | `background`; keep consistent across screens unless a deliberate surface distinction is needed. |
| Cards, sheets, dialogs, fields | `surface`/`onSurface`; use `surfaceVariant` for grouped or lower-emphasis areas. |
| Main body text | `onSurface` or `onBackground`; use `onSurfaceVariant` for secondary text only when its contrast remains sufficient. |
| Input outlines and focus | `outline` for important boundaries; use a clear focus indicator with sufficient contrast. `outlineVariant` is for quiet separators. |
| Error and destructive states | `error`/`onError`, or the matching error-container pair. Confirm destructive actions with text, not color alone. |
| Success, warning, information | Use the semantic token pairs above with labels/icons. Do not conflate payment status with button or selection state. |
| Disabled controls | Use Material 3 disabled-state treatment and retain readable labels. Verify contrast rather than assuming reduced alpha is acceptable. |
| Bottom/navigation surfaces | Use `surface` and corresponding content roles. Preserve active/inactive distinction with icon and label, not tint alone. |
| Original bright orange | Brand flourish or low-emphasis tint with a dark foreground; not white-label CTA fill. |

Prefer Material 3 component defaults driven by `MaterialTheme.colorScheme`. Avoid assigning raw `Color(...)` values inside individual composables. The existing named shade constants may be retained temporarily during migration, but they should resolve to approved semantic roles/tokens instead of creating parallel palettes.

## 7. Recommended migration sequence

1. **Approve this palette before code changes.** Confirm the deeper action orange and whether teal is acceptable as a tertiary/status-support color.
2. **Create one source of truth in the theme package.** Replace default purple role mappings with the approved Light and Dark `ColorScheme`; define semantic success, warning, and information tokens alongside the scheme.
3. **Select by system theme.** Use the branded light or dark scheme based on `isSystemInDarkTheme()`. Keep dynamic color off by default to preserve brand and operational status colors.
4. **Map legacy tokens carefully.** Replace existing imports such as `Primary`, `Primary100`, `Secondary100`, `Danger*`, `Success`, and `Info` with the correct semantic role. Do not mechanically map every old shade: audit foreground/background use because the meaning and contrast may differ.
5. **Remove inline UI colors.** Consolidate literal `Color(0x...)` values, including ad hoc red, gray, and pale orange values, into theme or semantic tokens.
6. **Check component states.** Review buttons, cards, text fields, dialogs, sheets, drawer/navigation surfaces, status badges, focus, selected, disabled, loading, and error states in both themes.
7. **Verify accessibility and visuals.** Recalculate contrast on actual rendered pairs, inspect 100%/large font sizes and compact/expanded layouts, and test with TalkBack. Status and selection must remain clear without color perception.

No Kotlin source changes or screen redesigns are part of this proposal.

## References

- [Material 3 theming in Jetpack Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Material Design 3 color system](https://m3.material.io/styles/color/overview)
- [WCAG 2.2, Success Criterion 1.4.3: Contrast (Minimum)](https://www.w3.org/TR/WCAG22/#contrast-minimum)
- [WCAG 2.2, Success Criterion 1.4.11: Non-text Contrast](https://www.w3.org/TR/WCAG22/#non-text-contrast)
- Repository guidance: `.github/prompts/ui-ux-pro-max/PROMPT.md`, `data/colors.csv`, and `data/stacks/jetpack-compose.csv`
