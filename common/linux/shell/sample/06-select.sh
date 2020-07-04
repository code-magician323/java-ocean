#!/bin/bash

select ch in "begin" "end" "exit"; do
    case $ch in
        "begin")
            echo "start something"  
        ;;
        "end")
            echo "stop something"  
        ;;
        "exit")
            echo "exit"  
            break;
        ;;
        *)
            echo "Ignorant"  
        ;;
    esac
done;
