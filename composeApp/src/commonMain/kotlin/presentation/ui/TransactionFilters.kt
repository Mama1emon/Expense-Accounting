package presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun TransactionFilters(
    categories: ImmutableSet<String>,
    onCategoryFilterClick: (String?) -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(value = false) }
    var category: String? by remember { mutableStateOf(value = null) }

    FilterChip(
        selected = category != null,
        onClick = {
            if (category != null) {
                category = null
                onCategoryFilterClick(null)
            } else {
                isMenuExpanded = true
            }
        },
        label = {
            AnimatedContent(category) { animatedCategory ->
                Text(text = animatedCategory ?: "Category")
            }
        },
        modifier = Modifier.animateContentSize(),
        trailingIcon = {
            AnimatedContent(category != null) {
                Icon(
                    imageVector = if (it) {
                        Icons.Outlined.Close
                    } else {
                        Icons.Outlined.ArrowDropDown
                    },
                    contentDescription = null
                )
            }
        },
    )

    SelectMenu(
        isExpanded = isMenuExpanded,
        items = categories,
        onSelect = {
            category = it
            onCategoryFilterClick(it)
            isMenuExpanded = false
        },
        onDismissRequest = {
            isMenuExpanded = false
        }
    )
}