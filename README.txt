There are 8 java files in the src folder. To compile them, please use command “javac *.java”. 

The file UserBasedPrediction.java implements the User-based Prediction algorithm. The file SVMDataPreProcessor.java is responsible to preprocess the training data and test data and prepare them for libsvm. 

ml-100k is the data this project is using. 


How to run the program:
======================
1) User-based Prediction
On command line, type ./UserBasedPrediction train_file test_file
The program takes less than one minute to finish. You will see the RMSE value in output.


2) SVM
Use the open source tool libsvm. Below are several steps for training and predicting:
2.1) Prepare the training and test data
=======================================
Libsvm accepts a different format of training/test data. We must use SVMDataPreProcessor.java to prepare the original movie-lens dataset to make them acceptable by libsvm.

To do that, please type ./SVMDataPreProcessor <user_info_file> <movie_info_file> <train_file> <test_file>
After the program finish, you will two newly generated training file and test file, they are named as new_<train_file> and new_<test_file>. 

2.2) Train with libsvm
======================
Download libsvm. Put the generated training and testing file in the libsvm folder. Compile the libsvm code. 
Type ./svm-train new_<train_file> 

After this step finish, you will get a file called new_<train_file>.model which is the generated model.

2.3) Predict with libsvm
Type ./svm-predict new_<test_file> new_<train_file>.model output_file
This will compute and print predicted values into the output_file

2.4) Calculate RMSE
Use CalculateRMSE.java to calculate the RMSE. 
Type command ./CalculateRMSE test_file output_file
 

