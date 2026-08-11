/**
 * v-draggable — Drag-to-reorder directive for lists
 * Usage: v-draggable="{ list, onReorder }"
 *   list: reactive array to reorder
 *   onReorder(newList): called after drag completes
 *
 * Requires CSS: .dragging { opacity: 0.5; }
 *                .drag-over { border-top: 2px solid var(--color-primary); }
 */
let dragSource = null
let dragIndex = -1

function getDragAfterElement(container, y) {
  const draggableElements = [...container.querySelectorAll('[data-drag-item]:not(.dragging)')]
  return draggableElements.reduce((closest, child) => {
    const box = child.getBoundingClientRect()
    const offset = y - box.top - box.height / 2
    if (offset < 0 && offset > closest.offset) {
      return { offset, element: child }
    }
    return closest
  }, { offset: Number.NEGATIVE_INFINITY }).element
}

export default {
  mounted(el, binding) {
    const { list, onReorder, handle = null } = binding.value || {}

    if (!list) {
      console.warn('[v-draggable] requires a reactive list. Usage: v-draggable="{ list }"')
      return
    }

    el.addEventListener('dragstart', (e) => {
      const item = e.target.closest('[data-drag-item]')
      if (!item) return
      if (handle && !e.target.closest(handle)) return

      dragSource = el
      dragIndex = [...el.querySelectorAll('[data-drag-item]')].indexOf(item)
      item.classList.add('dragging')
      e.dataTransfer.effectAllowed = 'move'
      e.dataTransfer.setData('text/plain', '')  // Required for Firefox
    })

    el.addEventListener('dragenter', (e) => {
      e.preventDefault()
      const item = e.target.closest('[data-drag-item]')
      if (item) item.classList.add('drag-over')
    })

    el.addEventListener('dragover', (e) => {
      e.preventDefault()
      const afterElement = getDragAfterElement(el, e.clientY)
      const dragging = el.querySelector('.dragging')
      if (!dragging) return

      if (!afterElement) {
        el.appendChild(dragging)
      } else {
        el.insertBefore(dragging, afterElement)
      }
    })

    el.addEventListener('dragleave', (e) => {
      const item = e.target.closest('[data-drag-item]')
      if (item) item.classList.remove('drag-over')
    })

    el.addEventListener('drop', (e) => {
      e.preventDefault()
      const dragging = el.querySelector('.dragging')
      if (dragging) {
        dragging.classList.remove('dragging')
        el.querySelectorAll('.drag-over').forEach(c => c.classList.remove('drag-over'))

        // Update the actual array order
        const newOrder = [...el.querySelectorAll('[data-drag-item]')].map(
          item => list[Number(item.dataset.dragItem)]
        )
        list.splice(0, list.length, ...newOrder)

        if (onReorder) onReorder([...list])
      }
    })

    // Store cleanup reference
    el._draggableCleanup = true
  }
}
