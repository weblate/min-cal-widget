// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.SymbolSet
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.*
import java.time.DayOfWeek
import java.util.stream.Stream

internal class ConfigurationTest : BaseTest() {

    @ParameterizedTest
    @EnumSource(value = Theme::class)
    fun getCalendarTheme_shouldReturnSharedPreferencesValue(theme: Theme) {
        mockSharedPreferences()
        mockCalendarTheme(theme)

        val result = Configuration.CalendarTheme.get(context)

        assertThat(result).isEqualTo(theme)
        verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun getFirstDayOfWeek_shouldReturnSharedPreferencesValue(dayOfWeek: DayOfWeek) {
        mockSharedPreferences()
        mockFirstDayOfWeek(dayOfWeek)

        val result = Configuration.FirstDayOfWeek.get(context)

        assertThat(result).isEqualTo(dayOfWeek)
        verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = SymbolSet::class)
    fun getInstancesSymbolSet_shouldReturnSharedPreferencesValue(symbolSet: SymbolSet) {
        mockSharedPreferences()
        mockInstancesSymbolSet(symbolSet)

        val result = Configuration.InstancesSymbolSet.get(context)

        assertThat(result).isEqualTo(symbolSet)
        verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = Colour::class)
    fun getInstancesColour_shouldReturnSharedPreferencesValue(colour: Colour) {
        mockSharedPreferences()
        mockInstancesColour(colour)

        val result = Configuration.InstancesColour.get(context)

        assertThat(result).isEqualTo(colour)
        verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfConfigurationItemsWithValuesAndKey")
    fun <E : Enum<E>> getConfigurationItem_shouldReturnValueFromOrdinal(
        values: Array<E>,
        item: Configuration<E>
    ) {
        var i = 0
        values.forEach { enumValue ->
            assertThat(item.get(i++)).isEqualTo(enumValue)
        }
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfConfigurationItemsWithValuesAndKey")
    fun <E : Enum<E>> setConfigurationItem_shouldPutEnumNameString(
        values: Array<E>,
        item: Configuration<E>,
        key: String
    ) {
        mockSharedPreferences()
        var invocations = 0
        values.forEach { enumValue ->
            item.set(context, enumValue)
            verify(editor, times(1)).putString(key, enumValue.name)
            invocations++
        }

        verify(editor, times(invocations)).apply()
        verifyNoMoreInteractions(editor)
    }

    @Test
    fun clearAllConfiguration_shouldRemoveAllApplicationPreferences() {
        mockSharedPreferences()
        clearAllConfiguration(context)

        verify(editor, times(1)).clear()
        verify(editor, times(1)).apply()
        verifyNoMoreInteractions(editor)
    }

    companion object {

        @JvmStatic
        @Suppress("unused", "LongMethod")
        fun getCombinationOfConfigurationItemsWithValuesAndKey(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Theme.values(),
                Configuration.CalendarTheme,
                "CALENDAR_THEME"
            ),
            Arguments.of(
                DayOfWeek.values(),
                Configuration.FirstDayOfWeek,
                "FIRST_DAY_OF_WEEK"
            ),
            Arguments.of(
                SymbolSet.values(),
                Configuration.InstancesSymbolSet,
                "INSTANCES_SYMBOL_SET"
            ),
            Arguments.of(
                Colour.values(),
                Configuration.InstancesColour,
                "INSTANCES_COLOUR"
            )
        )
    }
}