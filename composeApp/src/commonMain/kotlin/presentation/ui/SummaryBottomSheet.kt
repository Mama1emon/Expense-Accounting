package presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.state.MainScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryBottomSheet(
    summaryState: MainScreenState.SummaryState,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", style = MaterialTheme.typography.titleMedium)
                Text(text = summaryState.total)
            }

            Spacer(modifier = Modifier.height(8.dp))

            summaryState.totalCategories.forEach { totalCategory ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "– ${totalCategory.category}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(text = totalCategory.total)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}