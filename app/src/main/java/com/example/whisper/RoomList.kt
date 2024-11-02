package com.example.whisper

// RoomsList.kt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoomList(
    rooms: List<RoomData>,
    onLeaveRoom: (String) -> Unit,
    onRoomClick: (RoomData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rooms) { room ->
            RoomItem(
                room = room,
                onLeave = onLeaveRoom,
                onClick = onRoomClick
            )
        }
    }
}