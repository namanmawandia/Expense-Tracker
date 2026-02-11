package com.MStudios.monetrix

import Primary
import Primary2
import White
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class BarLayout {

    @Composable
    fun DateBar(
        month: String,
        year: String,
        onMonthChange: (String) -> Unit,
        onYearChange: (String) -> Unit,
        onPrev: () -> Unit,
        onNext: () -> Unit,
        onMenuClick: () -> Unit
    ) {
        val months = remember { months }
        val years = remember { years}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Primary),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                IconButton(onClick = onPrev) {
                    Icon(
                        painter = painterResource(id = R.drawable.noun_left_arrow_74837),
                        contentDescription = "Previous", tint = White
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                DropdownSpinner(
                    items = months,
                    selectedItem = month,
                    onItemSelected = onMonthChange
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownSpinner(
                    items = years,
                    selectedItem = year,
                    onItemSelected = onYearChange
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onNext) {
                    Icon(
                        painter = painterResource(id = R.drawable.noun_right_arrow_74838),
                        contentDescription = "Next", tint = White
                    )
                }
            }

            // Menu stays at the far right
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu", tint = White
                )
            }
        }
    }

    @Composable
    fun DropdownSpinner(
        items: List<String>,
        selectedItem: String,
        onItemSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            // Selected value (always visible)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(color = White, text = selectedItem)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null, tint = Primary2
                )
            }

            // Dropdown list
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            expanded = false
                            onItemSelected(item)
                        }
                    )
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DateBarPreview() {
        var selectedMonth by remember { mutableStateOf("January") }
        var selectedYear by remember { mutableStateOf("2026") }
        DateBar(
            month = selectedMonth,
            year = selectedYear,
            onMonthChange = { newMonth ->
                selectedMonth = newMonth
                println("Selected month: $newMonth")
            },
            onYearChange = { newYear ->
                selectedYear = newYear
                println("Selected year: $newYear")
            },
            onPrev = {},
            onNext = {},
            onMenuClick = {}
        )
    }
}
