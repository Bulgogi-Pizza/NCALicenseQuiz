import React, { useState } from "react";
import { Dialog, DialogTitle, Button, RadioGroup, FormControlLabel, Radio } from "@mui/material";

const questions = [
  [
    {
      "question": "Public IP에 대한 설명 중 틀린 것은?",
      "options": [
        "서버당 1개의 Public IP만 부여가 가능하다.",
        "Outbound시에는 NCP NAT IP로 통신한다.",
        "Classic 환경에서는 KR-1 존의 Public IP는 KR-2 존의 서버에 부여가 불가능하다.",
        "서버를 반납해도 Public IP는 반납되지 않는다."
      ],
      "answer": 1,
      "hint": null
    },
    {
      "question": "다음중 네이버 클라우드 플랫폼에서 제공하는 상품이 아닌 것은?",
      "options": [
        "Cloud Functions",
        "Container Registry",
        "Outpost",
        "SourcePipeline"
      ],
      "answer": 2,
      "hint": null
    },
    {
      "question": "네이버 클라우드 플랫폼에는 다양한 상품들이 카테고리로 그룹화되어 있습니다. \"CloudHadoop\" 상품은 어떤 카테고리에 속해 있을까요?",
      "options": [
        "Compute",
        "Database",
        "Analytics",
        "AI-Application Service"
      ],
      "answer": 2,
      "hint": null
    }
  ]
];

const QuizApp = () => {
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isCorrect, setIsCorrect] = useState(false);

  const handleOptionChange = (event) => {
    setSelectedOption(parseInt(event.target.value, 10));
  };

  const handleSubmit = () => {
    const isAnswerCorrect = selectedOption === questions[currentQuestionIndex].answer;
    setIsCorrect(isAnswerCorrect);
    setIsDialogOpen(true);
  };

  const handleNextQuestion = () => {
    setIsDialogOpen(false);
    setSelectedOption(null);
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  return (
      <div>
        <h2>{questions[currentQuestionIndex].question}</h2>
        <RadioGroup onChange={handleOptionChange}>
          {questions[currentQuestionIndex].options.map((option, index) => (
              <FormControlLabel
                  key={index}
                  value={index}
                  control={<Radio />}
                  label={option}
              />
          ))}
        </RadioGroup>
        <Button variant="contained" onClick={handleSubmit} disabled={selectedOption === null}>
          제출하기
        </Button>

        <Dialog open={isDialogOpen} onClose={handleNextQuestion}>
          <DialogTitle>{isCorrect ? "정답입니다!" : "오답입니다."}</DialogTitle>
          <Button onClick={handleNextQuestion}>다음 문제</Button>
        </Dialog>
      </div>
  );
};

export default QuizApp;