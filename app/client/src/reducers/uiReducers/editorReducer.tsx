import { createReducer } from "../../utils/AppsmithUtils";
import {
  ReduxActionTypes,
  ReduxAction,
  LoadCanvasWidgetsPayload,
  LoadWidgetCardsPanePayload,
} from "../../constants/ReduxActionConstants";
import { WidgetCardProps, WidgetProps } from "../../widgets/BaseWidget";
import { ContainerWidgetProps } from "../../widgets/ContainerWidget";

const initialState: EditorReduxState = {
  pageWidgetId: "0",
  currentPageId: "5d807e76795dc6000482bc76",
  currentLayoutId: "5d807e76795dc6000482bc75",
};

const editorReducer = createReducer(initialState, {
  [ReduxActionTypes.SUCCESS_FETCHING_WIDGET_CARDS]: (
    state: EditorReduxState,
    action: ReduxAction<LoadWidgetCardsPanePayload>,
  ) => {
    return { ...state, ...action.payload };
  },
  [ReduxActionTypes.LOAD_CANVAS_WIDGETS]: (
    state: EditorReduxState,
    action: ReduxAction<LoadCanvasWidgetsPayload>,
  ) => {
    return { ...state, pageWidgetId: action.payload.pageWidgetId };
  },
});

export interface EditorReduxState {
  dsl?: ContainerWidgetProps<WidgetProps>;
  cards?: {
    [id: string]: WidgetCardProps[];
  };
  pageWidgetId: string;
  currentPageId: string;
  currentLayoutId: string;
}

export default editorReducer;
