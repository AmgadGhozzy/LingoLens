package com.venom.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.venom.ui.components.sections.LangSelectorContent
import com.venom.ui.viewmodel.LangSelectorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangSelectorBottomSheet(
    viewModel: LangSelectorViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val configuration = LocalConfiguration.current

    val bottomSheetHeight = remember(configuration) {
        configuration.screenHeightDp * 0.8f
    }


    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = { scope.launch { sheetState.hide(); onDismiss() } },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier
            .fillMaxHeight(bottomSheetHeight)
            .navigationBarsPadding()
    ) {
        LangSelectorContent(
            state = state,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onLanguageSelected = { language ->
                viewModel.onLanguageSelected(language)
                scope.launch { sheetState.hide(); onDismiss() }
            },
        )
    }
}
