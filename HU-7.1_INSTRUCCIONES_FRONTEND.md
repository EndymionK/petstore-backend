# HU-7.1: Baja Visión (Inventario y Notificaciones)

## ✅ Estado: PENDIENTE (Requiere implementación en Frontend)

## 📋 Descripción

**Como administrador del inventario con baja visión**, quiero que los estados del inventario y las notificaciones sean legibles y distinguibles sin depender solo del color, para entender y actuar usando zoom y alto contraste.

## 🎯 Criterios de Aceptación

### HU-7.1-CA01: Texto y números visibles
**Objetivo:** Contraste ≥ 4.5:1 sobre su fondo

**Implementación:**
- Nombres de productos
- Cantidades de stock
- Texto "Stock bajo"
- Precios
- Nombres de proveedores

**Herramientas para validar:**
- [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)
- [WCAG Contrast Analyzer (Chrome Extension)](https://chrome.google.com/webstore/detail/wcag-contrast-checker/)

**Ejemplo de código:**
```css
/* Ejemplo: Contraste suficiente */
.product-name {
  color: #000000;  /* Negro */
  background-color: #FFFFFF;  /* Blanco */
  /* Ratio: 21:1 (AAA) */
}

/* Ejemplo: Alertas de stock bajo */
.stock-low {
  color: #FFFFFF;  /* Blanco */
  background-color: #D32F2F;  /* Rojo oscuro */
  /* Ratio: 7.3:1 (AAA) */
}
```

### HU-7.1-CA02: "Stock bajo" siempre con icono + texto
**Objetivo:** No solo con color

**Implementación:**
```html
<!-- ❌ MAL: Solo color -->
<span class="stock-low-badge">Stock bajo</span>

<!-- ✅ BIEN: Icono + texto -->
<span class="stock-low-badge">
  <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16">
    <path d="M8 1L15 15H1L8 1Z" fill="currentColor"/>
  </svg>
  <span>Stock bajo</span>
</span>

<!-- ✅ BIEN: Alternativa con emoji (accesible) -->
<span class="stock-low-badge">
  <span aria-label="Alerta">⚠️</span>
  <span>Stock bajo</span>
</span>
```

**Nota:** El backend ya proporciona el campo `stockBajo` en la respuesta de productos.

```json
{
  "codigo": 1,
  "nombre": "Croquetas Premium",
  "stock": 5,
  "umbralMinimo": 10,
  "stockBajo": true  // ← El backend ya calcula esto
}
```

### HU-7.1-CA03: UI usable con zoom 200%
**Objetivo:** Sin pérdida de funcionalidad ni superposición crítica

**Implementación:**
```css
/* Usar unidades relativas (rem, em, %) */
.product-list {
  font-size: 1rem;  /* No usar px fijos */
  padding: 1rem;
  gap: 1rem;
}

/* Layout flexible */
.product-card {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  max-width: 100%;
}

/* Evitar posicionamiento absoluto que cause superposición */
.stock-low-badge {
  position: relative;  /* No usar absolute en elementos críticos */
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}
```

**Prueba manual:**
1. Abrir Chrome DevTools (F12)
2. Ir a "Responsive Design Mode" (Ctrl+Shift+M)
3. Configurar zoom: 200%
4. Verificar que no haya elementos superpuestos
5. Verificar que todos los botones sean clicables

### HU-7.1-CA03: Ancho 320 CSS px (móvil) sin scroll horizontal
**Objetivo:** Contenido principal sin scroll horizontal

**Implementación:**
```css
/* Contenedor principal */
.container {
  width: 100%;
  max-width: 100vw;  /* Evitar overflow */
  overflow-x: hidden;  /* Forzar sin scroll horizontal */
  padding: 0.5rem;
}

/* Tabla responsive */
.table-responsive {
  width: 100%;
  overflow-x: auto;  /* Scroll solo en tablas grandes */
  display: block;
}

/* Grid responsive */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
  width: 100%;
}

/* Evitar overflow en inputs */
input, select, textarea {
  max-width: 100%;
  box-sizing: border-box;
}
```

**Prueba manual:**
1. Abrir Chrome DevTools (F12)
2. Responsive Design Mode (Ctrl+Shift+M)
3. Configurar ancho: 320px
4. Verificar que no aparezca scroll horizontal
5. Todos los elementos deben ser visibles

## 🧪 Checklist de Validación

- [ ] **Contraste:** Todos los textos tienen ≥ 4.5:1
- [ ] **Stock bajo:** Icono + texto visible (no solo color)
- [ ] **Zoom 200%:** UI funcional sin superposiciones
- [ ] **320px:** Sin scroll horizontal en contenido principal
- [ ] **Iconos:** `aria-hidden="true"` o `aria-label` adecuado
- [ ] **Color:** No es el único indicador de información

## 🔗 Recursos de Implementación

### Documentación
- [WCAG 2.1 - Contraste (Nivel AA)](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html)
- [WCAG 2.1 - Zoom al 200%](https://www.w3.org/WAI/WCAG21/Understanding/reflow.html)
- [WCAG 2.1 - Sin pérdida de información](https://www.w3.org/WAI/WCAG21/Understanding/reflow.html)

### Herramientas
- [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)
- [axe DevTools (Chrome Extension)](https://chrome.google.com/webstore/detail/axe-devtools-web-accessib/lhdoppojpmngadmnindnejefpokejbdd)
- [WAVE (Chrome Extension)](https://chrome.google.com/webstore/detail/wave-evaluation-tool/jbbplnpkjmmeebjpijfedlgcdilocofh)

### Ejemplos de Implementación
- [Material Design - Accessibility](https://material.io/design/usability/accessibility.html)
- [A11y Project](https://www.a11yproject.com/)

## 📝 Notas para Desarrolladores Frontend

### API del Backend

El backend ya proporciona el campo `stockBajo` calculado automáticamente:

```bash
GET /api/inventory/productos
Authorization: Bearer <token>
```

```json
[
  {
    "codigo": 1,
    "nombre": "Croquetas Premium",
    "stock": 5,
    "precio": 29.99,
    "proveedor": "PetFood Supply",
    "umbralMinimo": 10,
    "stockBajo": true  // ← Usa este campo para mostrar el indicador
  }
]
```

### Recomendación de Stack

Si estás usando React, Vue, Angular, etc., considera:

1. **Librerías de UI accesibles:**
   - React: Material-UI, Chakra UI, React Aria
   - Vue: Vuetify, Vue A11y
   - Angular: Angular Material

2. **Iconos accesibles:**
   - [Heroicons](https://heroicons.com/) - Incluye ARIA labels
   - [Feather Icons](https://feathericons.com/) - Sencillo y accesible
   - [Font Awesome](https://fontawesome.com/) - Con soporte ARIA

3. **Ejemplo de componente (React):**
```jsx
function ProductCard({ product }) {
  return (
    <div className="product-card">
      <h3 className="product-name">{product.nombre}</h3>
      <p className="product-stock">
        Stock: <strong>{product.stock}</strong>
      </p>
      {product.stockBajo && (
        <div className="stock-low-badge" role="alert" aria-label="Alerta de stock bajo">
          <svg aria-hidden="true" width="16" height="16" viewBox="0 0 16 16">
            <path d="M8 1L15 15H1L8 1Z" fill="currentColor"/>
          </svg>
          <span>Stock bajo</span>
        </div>
      )}
    </div>
  );
}
```

## ✅ Listo para Frontend

El backend ya está preparado con:
- ✅ Campo `stockBajo` calculado
- ✅ Campo `umbralMinimo` configurable
- ✅ API REST lista para consumir
- ✅ Documentación completa en README.md

**Solo falta implementar la UI según los criterios de aceptación arriba.**

