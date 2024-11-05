package com.example.whisper.ui.screens.chat.components

// RoomsList.kt
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whisper.data.model.RoomData
import com.example.whisper.utils.getCurrentTime

@Composable
fun RoomList(
    rooms: List<RoomData>,
    onLeaveRoom: (String) -> Unit,
    onRoomClick: (RoomData) -> Unit,
    isPublic: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rooms) { room ->
            if (room.expiresAt > getCurrentTime()) {
                RoomItem(
                    room = room,
                    onLeave = onLeaveRoom,
                    onClick = onRoomClick,
                    isPublic
                )
            }
            else {
                onLeaveRoom(room.id)
                Log.d("Room Expired", "Room expired. Leaving room.")
            }
        }
    }
}