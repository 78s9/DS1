/**
 * useDragDrop — Drag and drop list reordering composable
 * Provides the logic layer for drag-and-drop reorder (UI-agnostic).
 *
 * @param {Ref<Array>} itemsRef - the reactive array to reorder
 * @param {Function} onReorder - callback receiving the new array
 * @returns {{ draggingIndex, onDragStart, onDragOver, onDrop, resetDrag }}
 */
import { ref } from 'vue'

export function useDragDrop(itemsRef, onReorder) {
  const draggingIndex = ref(-1)
  const dragOverIndex = ref(-1)
  let draggedItem = null

  function onDragStart(index) {
    draggingIndex.value = index
    draggedItem = itemsRef.value[index]
  }

  function onDragOver(index, event) {
    event?.preventDefault()
    if (index === draggingIndex.value) return
    dragOverIndex.value = index
  }

  function onDragLeave() {
    dragOverIndex.value = -1
  }

  function onDrop(index, event) {
    event?.preventDefault()
    if (draggingIndex.value === -1 || draggingIndex.value === index) {
      resetDrag()
      return
    }

    // Reorder the array
    const newList = [...itemsRef.value]
    const [removed] = newList.splice(draggingIndex.value, 1)
    newList.splice(index, 0, removed)

    // Update reactive array
    itemsRef.value.splice(0, itemsRef.value.length, ...newList)

    if (onReorder) onReorder([...newList])
    resetDrag()
  }

  function resetDrag() {
    draggingIndex.value = -1
    dragOverIndex.value = -1
    draggedItem = null
  }

  return {
    draggingIndex,
    dragOverIndex,
    onDragStart,
    onDragOver,
    onDragLeave,
    onDrop,
    resetDrag
  }
}
