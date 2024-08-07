package presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import presentation.state.MainScreenState

@Composable
fun TransactionItem(
    state: MainScreenState.TransactionItemState.Transaction,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = state.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W500
                )
            )

            Text(
                text = state.amount,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W400
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = state.category,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )

            Text(
                text = state.primaryAmountString,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
