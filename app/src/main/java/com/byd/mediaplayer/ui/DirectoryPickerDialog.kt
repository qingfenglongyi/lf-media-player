package com.byd.mediaplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byd.mediaplayer.util.Logger
import java.io.File

/**
 * 简易文件浏览器对话框
 * 用于选择音乐目录，替代不支持的车机SAF
 *
 * @param initialPath 起始目录路径
 * @param onDismiss 关闭对话框回调
 * @param onDirectorySelected 选中目录回调，返回目录绝对路径
 */
@Composable
fun DirectoryPickerDialog(
    initialPath: String = "/sdcard",
    onDismiss: () -> Unit,
    onDirectorySelected: (String) -> Unit
) {
    var currentPath by remember { mutableStateOf(initialPath) }
    var subDirs by remember { mutableStateOf(listOf<File>()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun loadDirs(path: String) {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) {
            errorMsg = "目录不存在"
            subDirs = emptyList()
            return
        }
        val dirs = dir.listFiles()
            ?.filter { it.isDirectory && !it.isHidden }
            ?.sortedBy { it.name.lowercase() }
            ?: emptyList()
        subDirs = dirs
        errorMsg = null
    }

    LaunchedEffect(currentPath) {
        loadDirs(currentPath)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("选择音乐目录", fontSize = 18.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 当前路径
                Text(
                    text = currentPath,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                errorMsg?.let { msg ->
                    Text(
                        text = msg,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 上一级按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val parent = File(currentPath).parent
                            if (parent != null) {
                                currentPath = parent
                            }
                        }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📁 ..", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("上一级", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // 子目录列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    items(subDirs, key = { it.absolutePath }) { dir ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentPath = dir.absolutePath }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📁", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dir.name,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Logger.i("DirectoryPicker", "选择目录: $currentPath")
                onDirectorySelected(currentPath)
            }) {
                Text("选择此目录")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
