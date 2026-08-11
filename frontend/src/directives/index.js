/**
 * Global custom directives registration
 */
import intersect from './intersect'
import clickOutside from './clickOutside'
import ripple from './ripple'
import autofocus from './autofocus'
import draggable from './draggable'

export function registerDirectives(app) {
  app.directive('intersect', intersect)
  app.directive('click-outside', clickOutside)
  app.directive('ripple', ripple)
  app.directive('autofocus', autofocus)
  app.directive('draggable', draggable)
}

export { intersect, clickOutside, ripple, autofocus, draggable }
