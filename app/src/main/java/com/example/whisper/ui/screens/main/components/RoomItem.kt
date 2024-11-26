package com.example.whisper.ui.screens.main.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.whisper.R
import com.example.whisper.data.model.RoomData
import com.example.whisper.utils.formatDateTime

@Composable
fun RoomItem(
    room: RoomData,
    onLeave: (String) -> Unit,
    onClick: (RoomData) -> Unit,
    isPublic: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick(room) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.code, room.code),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.expires_at, formatDateTime(room.expiresAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // MainViewModel.joinRoom() instantiates a val room of RoomData(),
                // it does not set public attrib and it will therefore default to false
                // The local instances of these joined rooms will then have erroneous
                // public attributes but because we do not resync (edit?) rooms from clients
                // this is not an issue
                Log.d("DEBUG", "${room.public}")
                if (!isPublic) {
                    IconButton(
                        onClick = { onLeave(room.id) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Leave Room",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}