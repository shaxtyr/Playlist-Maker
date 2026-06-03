package com.practicum.playlistmaker.setting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.setting.domain.model.ThemeSettings

@Composable
fun SettingScreenCompose(viewModel: SettingsViewModel) {

    val themeSettings by viewModel.observeThemeSettings().observeAsState(initial = ThemeSettings(false))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (themeSettings.isDarkTheme) colorResource(id = R.color.black) else colorResource(id = R.color.white))
    ) {
        TopAppBar(
            title = { Text(
                text = stringResource(id = R.string.settings),
                fontSize = dimensionResource(id = R.dimen.medium_font_22).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.black)
                ) },
            backgroundColor = if (themeSettings.isDarkTheme) colorResource(id = R.color.black) else colorResource(id = R.color.white),
            elevation = 0.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(61.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.dark_theme_settings),
                fontSize = dimensionResource(id = R.dimen.medium_font_16).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                modifier = Modifier.padding(end = 8.dp),
                checked = themeSettings.isDarkTheme,
                onCheckedChange = { newValue ->
                    viewModel.switchTheme(newValue)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(id = R.color.blue),
                    uncheckedThumbColor = colorResource(id = R.color.gray),
                    checkedTrackColor = colorResource(id = R.color.blue_light),
                    uncheckedTrackColor = colorResource(id = R.color.light_gray)
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(61.dp)
                .clickable {
                    viewModel.shareApp()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.share_app_settings),
                fontSize = dimensionResource(id = R.dimen.medium_font_16).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier
                    .padding(end = 12.dp),
                painter = painterResource(id = R.drawable.ic_share_24),
                contentDescription = null,
                tint = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.gray)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(61.dp)
                .clickable {
                    viewModel.writeToSupport()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.write_to_team_settings),
                fontSize = dimensionResource(id = R.dimen.medium_font_16).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier
                    .padding(end = 12.dp),
                painter = painterResource(id = R.drawable.ic_support_24),
                contentDescription = null,
                tint = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.gray)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(61.dp)
                .clickable {
                    viewModel.showUserDoc()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.user_doc_settings),
                fontSize = dimensionResource(id = R.dimen.medium_font_16).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier.padding(end = 12.dp),
                painter = painterResource(id = R.drawable.ic_arrow_forward_24),
                contentDescription = null,
                tint = if (themeSettings.isDarkTheme) colorResource(id = R.color.white) else colorResource(id = R.color.gray)
            )
        }

    }
}
