compile: bin
	scalac -d ./bin HSTParse.scala

run: bin
	scala -cp ./bin HSTParse

clean: bin
	rm bin/*

bin:
	mkdir $@