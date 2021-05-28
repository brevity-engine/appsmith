import React, { useState } from "react";
import styled from "styled-components";
import { Size } from "components/ads/Button";
import { StyledDialog, ForkButton, ButtonWrapper } from "./ForkModalStyles";
import Checkbox from "components/ads/Checkbox";
import Text, { TextType } from "components/ads/Text";
import {
  EXPORT_APPLICATION_MODAL_TITLE,
  EXPORT_APPLICATION_MODAL_SUB_TITLE,
} from "constants/messages";

const CheckboxDiv = styled.div`
  // overflow: auto;
  max-height: 250px;
  margin-bottom: 10px;
  margin-top: 20px;
`;

type ExportApplicationModalProps = {
  applicationId?: string;
  applicationName?: string;
  organizationId?: string;
  isModalOpen?: boolean;
  setModalClose?: (isOpen: boolean) => void;
};

function ExportApplicationModal(props: ExportApplicationModalProps) {
  const { isModalOpen, setModalClose } = props;
  const onExportSuccess = () => {
    setModalClose && setModalClose(false);
  };

  const [isChecked, setIsCheckedToTrue] = useState(false);
  return (
    <StyledDialog
      canOutsideClickClose
      className={"t--export-application-modal"}
      isOpen={isModalOpen}
      maxHeight={"540px"}
      setModalClose={setModalClose}
      title={EXPORT_APPLICATION_MODAL_TITLE()}
    >
      <CheckboxDiv>
        <Text type={TextType.P1}>
          <Checkbox
            cypressSelector="t--export-app-confirm"
            label={EXPORT_APPLICATION_MODAL_SUB_TITLE()}
            onCheckChange={(checked: boolean) => {
              setIsCheckedToTrue(checked);
            }}
          />
        </Text>
      </CheckboxDiv>
      <ButtonWrapper>
        <ForkButton
          cypressSelector={"t--export-app-button"}
          disabled={!isChecked}
          href={`/api/v1/applications/export/${props.applicationId}`}
          onClick={onExportSuccess}
          size={Size.large}
          tag="a"
          target="_blank"
          text={"EXPORT"}
        />
      </ButtonWrapper>
    </StyledDialog>
  );
}

export default ExportApplicationModal;
