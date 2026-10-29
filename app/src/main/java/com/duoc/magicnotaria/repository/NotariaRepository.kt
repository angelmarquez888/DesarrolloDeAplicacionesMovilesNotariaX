package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.model.CategoriaTramite
import com.duoc.magicnotaria.model.Documento

class NotariaRepository {

    fun obtenerCategorias(): List<CategoriaTramite> = listOf(
        CategoriaTramite(
            "Poderes y Mandatos",
            listOf("Emitir Poder Simple", "Mandato General")
        ),
        CategoriaTramite(
            "Vehículos",
            listOf("Transferencia de Vehículos")
        ),
        CategoriaTramite(
            "Certificaciones",
            listOf("Salvoconducto Mudanza", "Autenticación de Firmas", "Declaración Jurada")
        )
    )

    fun obtenerDocumentos(): List<Documento> = listOf(
        Documento(1, "Compraventa de Vehículo", "Ref: 0984-2026 • 12 Oct 2026", true),
        Documento(2, "Declaración Jurada Simple", "Ref: 0112-2026 • 05 Sep 2026", true),
        Documento(3, "Poder Notarial Amplio", "Ref: 1045-2026 • En proceso", false)
    )
}