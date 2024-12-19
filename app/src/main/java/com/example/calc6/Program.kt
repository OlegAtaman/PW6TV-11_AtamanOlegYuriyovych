package com.example.powercalc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt
import kotlin.math.ceil

@Stable
data class DeviceSpecs(
    var title: String = "",
    var efficiencyRate: String = "",
    var powerRatio: String = "",
    var loadVoltage: String = "",
    var deviceCount: String = "",
    var nominalCapacity: String = "",
    var utilizationRate: String = "",
    var reactiveRate: String = "",
    var aggregatedPower: String = "",
    var operationalCurrent: String = "",
)

@Composable
fun DeviceForm(device: DeviceSpecs, onChange: (DeviceSpecs) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Text("Назва пристрою:")
        OutlinedTextField(
            value = device.title,
            onValueChange = { onChange(device.copy(title = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Назва для введення*") }
        )
        Text("ККД пристрою:")
        OutlinedTextField(
            value = device.efficiencyRate,
            onValueChange = { onChange(device.copy(efficiencyRate = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*ККД для введення*") }
        )
        Text("Коефіцієнт потужності:")
        OutlinedTextField(
            value = device.powerRatio,
            onValueChange = { onChange(device.copy(powerRatio = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Коеф для введення*") }
        )
        Text("Напруга живлення:")
        OutlinedTextField(
            value = device.loadVoltage,
            onValueChange = { onChange(device.copy(loadVoltage = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Напруга для введення*") }
        )
        Text("Кількість пристроїв:")
        OutlinedTextField(
            value = device.deviceCount,
            onValueChange = { onChange(device.copy(deviceCount = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Кількість для введення*") }
        )
        Text("Номінальна потужність:")
        OutlinedTextField(
            value = device.nominalCapacity,
            onValueChange = { onChange(device.copy(nominalCapacity = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Потужність для введення*") }
        )
        Text("Коефіцієнт використання:")
        OutlinedTextField(
            value = device.utilizationRate,
            onValueChange = { onChange(device.copy(utilizationRate = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Коефіцієнт для введення*") }
        )
        Text("Реактивна потужність:")
        OutlinedTextField(
            value = device.reactiveRate,
            onValueChange = { onChange(device.copy(reactiveRate = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("*Реактивна для введення*") }
        )
    }
}

@Preview
@Composable
fun MainInterface() {
    val focusHandler = LocalFocusManager.current
    val scrollController = rememberScrollState()

    var deviceList by remember {
        mutableStateOf(
            listOf(
                DeviceSpecs(
                    title = "Шліфмашина",
                    efficiencyRate = "0.92",
                    powerRatio = "0.9",
                    loadVoltage = "0.38",
                    deviceCount = "4",
                    nominalCapacity = "26",
                    utilizationRate = "0.15",
                    reactiveRate = "1.33"
                )
            )
        )
    }

    var aggregateUtilization by remember { mutableStateOf("1.25") }
    var groupCoefficient by remember { mutableStateOf("0.7") }

    var utilizationProductSum by remember { mutableStateOf(0.0) }

    var calculatedCoefficient by remember { mutableStateOf("") }
    var effectiveDeviceCount by remember { mutableStateOf("") }

    var totalActivePower by remember { mutableStateOf("") }
    var totalReactivePower by remember { mutableStateOf("") }
    var apparentPower by remember { mutableStateOf("") }
    var totalCurrent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollController)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            deviceList.forEachIndexed { idx, device ->
                DeviceForm(
                    device = device,
                    onChange = { updatedDevice ->
                        deviceList = deviceList.toMutableList().apply {
                            this[idx] = updatedDevice
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = { deviceList = deviceList + DeviceSpecs() },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text("Додати пристрій")
        }

        Button(
            onClick = {
                var usagePowerSum = 0.0
                var totalPowerSum = 0.0
                var squaredPowerSum = 0.0

                deviceList.forEach { unit ->
                    val quantity = unit.deviceCount.toDouble()
                    val power = unit.nominalCapacity.toDouble()
                    unit.aggregatedPower = "${quantity * power}"
                    val currentCalc = unit.aggregatedPower.toDouble() / (
                            sqrt(3.0) *
                                    unit.loadVoltage.toDouble() *
                                    unit.powerRatio.toDouble() *
                                    unit.efficiencyRate.toDouble()
                            )
                    unit.operationalCurrent = currentCalc.toString()

                    usagePowerSum += unit.aggregatedPower.toDouble() * unit.utilizationRate.toDouble()
                    totalPowerSum += unit.aggregatedPower.toDouble()
                    squaredPowerSum += quantity * power * power
                }

                utilizationProductSum = usagePowerSum

                val utilizationCoeff = usagePowerSum / totalPowerSum
                calculatedCoefficient = utilizationCoeff.toString()

                val effectiveCount = ceil(totalPowerSum * totalPowerSum / squaredPowerSum)
                effectiveDeviceCount = effectiveCount.toString()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обчислити")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Текст: Груповий коефіцієнт: $calculatedCoefficient")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Текст: Ефективна кількість: $effectiveDeviceCount")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = aggregateUtilization,
            onValueChange = { aggregateUtilization = it },
            label = { Text("Коеф активної потужності (Kr)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusHandler.clearFocus() }
        )

        Button(
            onClick = {
                val utilizationCoeff = calculatedCoefficient.toDoubleOrNull() ?: 0.0
                val loadCoeff = aggregateUtilization.toDoubleOrNull() ?: 0.0
                val refPower = 20.0
                val tanPhi = 1.55
                val voltageLevel = 0.38

                val activePowerCalc = loadCoeff * utilizationProductSum
                val reactivePowerCalc = utilizationCoeff * refPower * tanPhi
                val apparentPowerCalc = sqrt(activePowerCalc * activePowerCalc + reactivePowerCalc * reactivePowerCalc)
                val groupCurrentCalc = activePowerCalc / voltageLevel

                totalActivePower = activePowerCalc.toString()
                totalReactivePower = reactivePowerCalc.toString()
                apparentPower = apparentPowerCalc.toString()
                totalCurrent = groupCurrentCalc.toString()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обчислити навантаження")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Текст: Навантаження активне: $totalActivePower")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Текст: Навантаження реактивне: $totalReactivePower")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Текст: Навантаження повне: $apparentPower")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Текст: Струм групи: $totalCurrent")
    }
}
